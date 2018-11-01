package id.co.gets.myreport.main;

import android.app.Activity;
import android.content.ContentValues;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import id.co.gets.myreport.BuildConfig;
import id.co.gets.myreport.R;
import id.co.gets.myreport.dbhelper.DbHelper;
import id.co.gets.myreport.dbhelper.FeedReaderContract;
import id.co.gets.myreport.network.JSONParser;
import id.co.gets.myreport.network.SingletonRequestQueue;


public class MainActivity extends AppCompatActivity {

    TextView button, button1, button2, button3;
    Context mContext;
    Activity mActivity;
    CoordinatorLayout mCoordinatorLayout;
    DbHelper mDbHelper;
    String myId, namaVendor, reportApel, dataUser, emailUser, tetapKirim;
    ProgressBar pbApel, pbTko, pbMessage, pbMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        mActivity = MainActivity.this;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Home");
        setSupportActionBar(myToolbar);

        button = findViewById(R.id.main_btn_apel);
        button1 = findViewById(R.id.main_btn_tko);
        button2 = findViewById(R.id.main_btn_invoice);
        button3 = findViewById(R.id.main_btn_emergency);

        pbMain = findViewById(R.id.pb_main);
        pbMain.setVisibility(View.GONE);

        mDbHelper = new DbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM entry", null);
        cursor.moveToFirst();
        myId = "";

        if (cursor.moveToFirst()) {
            do {
                myId = cursor.getString(0);
                namaVendor = cursor.getString(cursor.getColumnIndex("vendor"));
                dataUser = cursor.getString(cursor.getColumnIndex("user"));
            } while (cursor.moveToNext());
        }
        cursor.close();

        String[] mydata = dataUser.split("&&");
        emailUser = mydata[5];

        //request data untuk update token dari maybank user(kebutuhan notifikasi)
        maybankUserGET();

        final DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apelReport();
            }
        });


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCoordinatorLayout = findViewById(R.id.main_view);

                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View apelView = inflater.inflate(R.layout.tko, null);

                final PopupWindow mPopupWindow1 = new PopupWindow(apelView,  dm.widthPixels*7/8, dm.heightPixels/2);


                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow1.setElevation(5.0f);
                }

                ImageButton closeButton = apelView.findViewById(R.id.tko_close);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopupWindow1.dismiss();
                    }
                });

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("MMMM");

               final TextView bulan = apelView.findViewById(R.id.tko_bulan);
               bulan.setText(mdformat.format(calendar.getTime()));

               final EditText minggu = apelView.findViewById(R.id.tko_week);
               final EditText shift = apelView.findViewById(R.id.tko_shift);
               final EditText nonshift = apelView.findViewById(R.id.tko_nonshift);

                pbTko = apelView.findViewById(R.id.pb_tko);
                pbTko.setVisibility(View.INVISIBLE);
                TextView btnKirim2 = apelView.findViewById(R.id.tko_btn_kirim);
                btnKirim2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       final String sBulan, sMinggu, sShift, sNonshift;

                        sBulan = bulan.getText().toString();
                        sMinggu = minggu.getText().toString();
                        sShift = shift.getText().toString();
                        sNonshift = nonshift.getText().toString();

                        if(sMinggu.equals("")){
                            Toast.makeText(getApplicationContext(), "Harap isi field minggu ke-", Toast.LENGTH_LONG).show();

                        } else if(sShift.equals("") || sNonshift.equals("")){
                            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                            alertDialog.setMessage("Isi Paket kosong, apakah yakin untuk di kirim?");
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    alertDialog.dismiss();
                                    pbTko.setVisibility(View.VISIBLE);
                                    tkoGET(namaVendor, sMinggu, sBulan, sShift, sNonshift, emailUser);
                                }
                            });
                            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                }
                            });
                            alertDialog.show();

                        } else {
                            pbTko.setVisibility(View.VISIBLE);
                            tkoGET(namaVendor, sMinggu, sBulan, sShift, sNonshift, emailUser);
                        }
                    }
                });

                mPopupWindow1.setFocusable(true);
                mPopupWindow1.update();
                mPopupWindow1.showAtLocation(mCoordinatorLayout, Gravity.CENTER,0,0);

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InvoiceActivity.class);
                intent.putExtra("nama_vendor", namaVendor);
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
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
        Toolbar tb = (Toolbar) findViewById(R.id.my_toolbar);
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
                Intent toAccount = new Intent(MainActivity.this, AccountActivity.class );
                startActivity(toAccount);
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

    private void onLogout(){
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Loging Out App");
        alertDialog.setMessage("Are you sure want to logout?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();

                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.execSQL("delete from entry where _ID = '" + myId + "'");

                Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
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
                    Toast.makeText(MainActivity.this, "Harap isi semua field", Toast.LENGTH_LONG).show();
                }else if( !newPassword.equals(confirmPass)){
                    Toast.makeText(MainActivity.this, "Password tidak sama", Toast.LENGTH_LONG).show();
                } else {
                    pbMain.setVisibility(View.VISIBLE);
                    ubahPassPOST(myData[5], newPassword);
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    private void sendEmergency(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.emergency, null);
        dialogBuilder.setView(dialogView);

        final Spinner spinner = (Spinner)dialogView.findViewById(R.id.sp_emergency);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.emergency,
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        final AutoCompleteTextView acRegion = dialogView.findViewById(R.id.emer_ac_region);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
                R.array.region, android.R.layout.simple_dropdown_item_1line);
        acRegion.setAdapter(adapter);

        final EditText etKci = dialogView.findViewById(R.id.emer_et_kci);

        final EditText etKcp = dialogView.findViewById(R.id.emer_et_kcp);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setTitle("Emergency Report");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Kirim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String type = spinner.getSelectedItem().toString();
                String region = acRegion.getText().toString();
                String kci = etKci.getText().toString();
                String kcp = etKcp.getText().toString();
                String message =  "Region: " + region + " KCI: " + kci +" KCP: " + kcp;
                if(region.equals("") || kci.equals("") || kcp.equals("")){
                    Toast.makeText(MainActivity.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                } else {
                    pbMain.setVisibility(View.VISIBLE);
                    notifPOST("[EMERGENCY] : "+ namaVendor, type +" "+message, emailUser);
                }
            }
        });
        alertDialog.show();
    }

    private void apelReport(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.apel_jaring, null);
        dialogBuilder.setView(dialogView);

        final AutoCompleteTextView acRegion = dialogView.findViewById(R.id.apel_ac_region);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
                R.array.region, android.R.layout.simple_dropdown_item_1line);
        acRegion.setAdapter(adapter);
        acRegion.setVisibility(View.GONE);

        final EditText etKci = dialogView.findViewById(R.id.apel_et_kci);
        etKci.setVisibility(View.GONE);

        final EditText etKcp = dialogView.findViewById(R.id.apel_et_kcp);
        etKcp.setVisibility(View.GONE);

        RadioGroup radioGroup = dialogView.findViewById(R.id.apel_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = dialogView.findViewById(checkedId);
                reportApel = radioButton.getText().toString();

                switch (reportApel) {
                    case "Tidak Lengkap":
                        reportApel = "1";
                        acRegion.setVisibility(View.VISIBLE);
                        etKci.setVisibility(View.VISIBLE);
                        etKcp.setVisibility(View.VISIBLE);
                        break;
                    case "Belum Lengkap":
                        reportApel = "2";
                        acRegion.setVisibility(View.VISIBLE);
                        etKci.setVisibility(View.VISIBLE);
                        etKcp.setVisibility(View.VISIBLE);
                        break;
                    case "Lengkap":
                        reportApel = "3";
                        acRegion.setVisibility(View.GONE);
                        etKci.setVisibility(View.GONE);
                        etKcp.setVisibility(View.GONE);
                        break;
                }

            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setTitle("Apel Jaring");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Kirim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String region = acRegion.getText().toString();
                String kci = etKci.getText().toString();
                String kcp = etKcp.getText().toString();
                tetapKirim = "no";

                if(reportApel.equals("2") || reportApel.equals("1")) {
                    if (!region.equals("") && !kci.equals("") && !kcp.equals("")) {
                        pbMain.setVisibility(View.VISIBLE);
                        ApelPOST(namaVendor, reportApel, emailUser, region, kci, kcp, tetapKirim);
                    } else {
                        Toast.makeText(MainActivity.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(reportApel !=null){
                        pbMain.setVisibility(View.VISIBLE);
                        ApelPOST(namaVendor, reportApel, emailUser, "", "", "", tetapKirim);
                    } else {
                        Toast.makeText(MainActivity.this, "Harap pilih kelengkapan", Toast.LENGTH_SHORT).show();
                    }
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

    private void maybankUserGET(){
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "base/tb_muser?id_muser=1";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                new JSONParser.ParseJson(response, 0);
                String[] maybankData = JSONParser.dataMaybank;

                StringBuilder sb = new StringBuilder();
                for(int i = 0; i<maybankData.length; i++){
                    sb.append(maybankData[i]).append("&&");
                }

                ContentValues values = new ContentValues();
                // Gets the data repository in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                values.put(FeedReaderContract.FeedEntry.COLUMN_MAYBANK_DATA, sb.toString());
                values.put(FeedReaderContract.FeedEntry.COLUMN_FB_TOKEN, maybankData[4]);
                db.update(FeedReaderContract.FeedEntry.TABLE_NAME, values,"_ID = ?",
                        new String[]{myId});

                if(maybankData[5].equals("ok")){
                    Toast.makeText(MainActivity.this, "Data Maybank berhasil diambil", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Gagal mengambil data Maybank", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Warning!! Anda tidak bisa mengirim Notifikasi", Toast.LENGTH_SHORT).show();
                }

            }
        }, errorListener){
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
                pbMain.setVisibility(View.GONE);
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


    private void ApelPOST(final String vendor, final String report, final String sender, final String region,
                          final String kci, final String kcp, final String kirimLagi) {

//        String printApel = vendor+report+sender+region+kci+kcp;
//        Log.e("Apel Print", printApel);
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "api/report";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");

                pbMain.setVisibility(View.INVISIBLE);
                if(response.contains("hari")){
                    final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle(response);
                    alertDialog.setMessage("Apakah yakin ingin mengirim report lagi?");
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            pbMain.setVisibility(View.VISIBLE);
                            tetapKirim = "yes";
                            ApelPOST(vendor, report, sender, region, kci, kcp, tetapKirim);
                        }
                    });

                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                } else {
                    tetapKirim = "no";
                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                }

            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }

            @Override
            public Map getParams() {
                Map params = new HashMap();


                params.put("nama_vendor", vendor);
                params.put("status_apel", report);
                if(!region.equals("")) {
                    String keterangan = "Region: " + region + ", KCP: " + kcp + ", KCI: " + kci;
                    params.put("ket", keterangan);
                } else if(region.equals("")) {
                    params.put("ket", "");
                }
                params.put("sender", sender);
                params.put("tetap_kirim", kirimLagi);

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

    private void tkoGET(final String vendor, final String week, final String bulan
            , final String shift, final String nonshift, final String sender){
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "api/report?month=" + bulan +
                "&week=" + week +"&nama_vendor=" + vendor +"&shift=" + shift + "&nonshift=" + nonshift+"&sender="+sender;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbTko.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }, errorListener){
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);

    }

    private void notifPOST(final String tipe, final String pesan, final String sender) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "api/notification";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbMain.setVisibility(View.INVISIBLE);
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
                params.put("nama_vendor", namaVendor);
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

}
