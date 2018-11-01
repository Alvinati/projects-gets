package id.co.gets.myreport.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import id.co.gets.myreport.BuildConfig;
import id.co.gets.myreport.R;
import id.co.gets.myreport.dbhelper.DbHelper;
import id.co.gets.myreport.network.SingletonRequestQueue;

public class Main2Activity extends AppCompatActivity {

    TextView button, button1;
    Context mContext;
    Activity mActivity;
    CoordinatorLayout mCoordinatorLayout;
    ProgressBar pbMessage, pbMain2;
    DbHelper mDbHelper;
    String[] mydata;
    String myId, namaCabang, dataUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        button = findViewById(R.id.main2_btn_das);
        button1 = findViewById(R.id.main2_btn_emergency);
        pbMain2 = findViewById(R.id.pb_main2);
        pbMain2.setVisibility(View.GONE);

        mContext = getApplicationContext();
        mActivity = Main2Activity.this;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main2_toolbar);
        myToolbar.setTitle("Home");
        setSupportActionBar(myToolbar);

        final DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);

        mDbHelper = new DbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM entry", null);
        cursor.moveToFirst();
        myId = "";

        if (cursor.moveToFirst()) {
            do {
                myId = cursor.getString(0);
                namaCabang = cursor.getString(cursor.getColumnIndex("vendor"));
                dataUser = cursor.getString(cursor.getColumnIndex("user"));
            } while (cursor.moveToNext());
        }
        cursor.close();

         mydata = dataUser.split("&&");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCoordinatorLayout = findViewById(R.id.main2_view);

                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View dasView = inflater.inflate(R.layout.message, null);

                final PopupWindow mPopupWindow = new PopupWindow(dasView,  dm.widthPixels*7/8, dm.heightPixels/2);

                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }

                ImageButton closeButton = dasView.findViewById(R.id.message_close);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopupWindow.dismiss();
                    }
                });

                final TextView etMessage = dasView.findViewById(R.id.message);
                TextView btnKirim = dasView.findViewById(R.id.message_btn_kirim);
                pbMessage = dasView.findViewById(R.id.pb_message);
                pbMessage.setVisibility(View.INVISIBLE);

                btnKirim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = etMessage.getText().toString();

                        if (message.equals("")) {
                            Toast.makeText(Main2Activity.this, "Harap isi field message", Toast.LENGTH_SHORT).show();
                        } else {
                            pbMessage.setVisibility(View.VISIBLE);
                            notifPOST("[DAS] from KCP:"+ namaCabang+ ", KCI: " + mydata[7], message, mydata[5]);
                        }
                    }
                });

                mPopupWindow.setFocusable(true);
                mPopupWindow.update();
                mPopupWindow.showAtLocation(mCoordinatorLayout, Gravity.CENTER,0,0);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmergency();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.main2_toolbar);
        tb.inflateMenu(R.menu.menu_main);
        tb.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });

        return true;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_account:
                Intent toAccount2 = new Intent(Main2Activity.this, Account2Activity.class);
                startActivity(toAccount2);
                break;
            case R.id.action_change_password:
                gantiPassword();
                break;
            case R.id.action_logout:
                onLogout();
                break;
            default:
                break;
        }

        return true;
    }

    private void sendEmergency(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.emergency, null);
        dialogBuilder.setView(dialogView);

        final EditText etMessaage = (EditText)findViewById(R.id.emer_et_message);
        etMessaage.setVisibility(View.GONE);

        final Spinner spinner = (Spinner)dialogView.findViewById(R.id.sp_emergency);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(Main2Activity.this, R.array.emergency,
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        final AutoCompleteTextView acRegion = dialogView.findViewById(R.id.emer_ac_region);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Main2Activity.this,
                R.array.region, android.R.layout.simple_dropdown_item_1line);
        acRegion.setAdapter(adapter);
        acRegion.setText(mydata[8]);

        final EditText etKci = dialogView.findViewById(R.id.emer_et_kci);
        etKci.setText(mydata[7]);

        final EditText etKcp = dialogView.findViewById(R.id.emer_et_kcp);
        etKcp.setText(mydata[3]);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setTitle("Emergency Report");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Kirim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String type = spinner.getSelectedItem().toString();
                String region = acRegion.getText().toString();
                String kci = etKci.getText().toString();
                String kcp = etKcp.getText().toString();
                String message =  "Region: " + region + ", KCI: " + kci +", KCP: " + kcp;
                if(region.equals("") || kci.equals("") || kcp.equals("")){
                    Toast.makeText(Main2Activity.this, "Harap isi field message", Toast.LENGTH_SHORT).show();
                } else {
                    pbMain2.setVisibility(View.VISIBLE);
                    notifPOST("[EMERGENCY] : "+ namaCabang, type +" di "+message, mydata[5]);
                }
            }
        });
        alertDialog.show();
    }

    private void onLogout(){
        final AlertDialog alertDialog = new AlertDialog.Builder(Main2Activity.this).create();
        alertDialog.setTitle("Loging Out App");
        alertDialog.setMessage("Are you sure want to logout?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();

                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.execSQL("delete from entry where _ID = '" + myId + "'");

                Intent toLogin = new Intent(Main2Activity.this, LoginActivity.class);
                toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                toLogin.putExtra("isLogedout", true);
                finish();
                startActivity(toLogin);
            }
        });

        alertDialog.show();

    }

    private void gantiPassword(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.ubah_password, null);
        dialogBuilder.setView(dialogView);

        final EditText editText = (EditText)dialogView.findViewById(R.id.new_pass);
        final EditText editText1 = (EditText)dialogView.findViewById(R.id.confirm_pass);

        final String[] myData = dataUser.split("&&");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setTitle("Ubah password");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ubah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String newPassword =  editText.getText().toString();
                String confirmPass = editText1.getText().toString();

                if (newPassword.equals("") || confirmPass.equals("")){
                    Toast.makeText(Main2Activity.this, "Harap isi semua field", Toast.LENGTH_LONG).show();
                }else if( !newPassword.equals(confirmPass)){
                    Toast.makeText(Main2Activity.this, "Password tidak sama", Toast.LENGTH_LONG).show();
                } else {
                    pbMain2.setVisibility(View.VISIBLE);
                    ubahPassPOST(myData[5], newPassword);
                    alertDialog.dismiss();
                    pbMain2.setVisibility(View.INVISIBLE);
                }
            }
        });
        alertDialog.show();
    }


    Response.ErrorListener errorListener = new
            Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof NetworkError) {
                        Toast.makeText(getApplicationContext(), "No network available", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };


    private void notifPOST(final String tipe, final String pesan, final String sender) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "api/notification";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbMain2.setVisibility(View.GONE);
                if (pbMessage != null) {
                    pbMessage.setVisibility(View.INVISIBLE);
                }
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.NORMAL;
            }

            @Override
            public Map getParams() {
                Map params = new HashMap();

                params.put("id_vendor", myId);
                params.put("nama_vendor", namaCabang);
                params.put("tipe_pesan", tipe);
                params.put("pesan", pesan);
                params.put("status", "0");
                params.put("sender", sender);

                return params;
            }

            @Override
            public Map getHeaders() throws
                    AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-TYpe", "application/x-www-form-urlencoded; charset=utf-8");
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    private void ubahPassPOST(final String email, final String passsword){
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "api/resetpass";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbMain2.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }

            @Override
            public Map getParams() {
                Map params = new HashMap();

                params.put("email", email);
                params.put("password", passsword);

                return params;
            }

            @Override
            public Map getHeaders() throws
                    AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-TYpe", "application/x-www-form-urlencoded; charset=utf-8");
                return headers;
            }
        };

        queue.add(stringRequest);

    }
}
