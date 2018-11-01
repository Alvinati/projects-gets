package id.co.mine.myreportadmin.main;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.mine.myreportadmin.BuildConfig;
import id.co.mine.myreportadmin.R;
import id.co.mine.myreportadmin.dbhelper.DbHelper;
import id.co.mine.myreportadmin.dfactory.JSONParser;
import id.co.mine.myreportadmin.dfactory.SingletonRequestQueue;
import id.co.mine.myreportadmin.fragments.NotifFragment;
import id.co.mine.myreportadmin.fragments.NotificationAdapter;
import id.co.mine.myreportadmin.fragments.ReportFragment;
import id.co.mine.myreportadmin.fragments.UsersFragment;
import id.co.mine.myreportadmin.services.MyFirebaseInstancedService;
import id.co.mine.myreportadmin.services.PeriodicService;

public class MainActivity extends AppCompatActivity {

    private ActionBar toolbar;
    boolean doubleClicktoExitPressedOnce = false;
    DbHelper mDbHelper;
    String myId, dataUser;
    String tokenRefresh;
    ProgressBar pbMain;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startService(new Intent(this, PeriodicService.class));

        toolbar = getSupportActionBar();
        toolbar.setTitle("Report");
        BottomNavigationView bottomNavigation = findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemReselectedListener);
        loadFragment(new ReportFragment());

        pbMain = findViewById(R.id.pb_main);
        pbMain.setVisibility(View.INVISIBLE);

        mDbHelper = new DbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM entry", null);
        cursor.moveToFirst();
        myId = "";

        if (cursor.moveToFirst()) {
            do {
                myId = cursor.getString(0);
                dataUser = cursor.getString(cursor.getColumnIndex("maybank"));
            } while (cursor.moveToNext());
        }
        cursor.close();



        tokenRefresh = MyFirebaseInstancedService.thisToken;
        if(tokenRefresh != null){
            Log.e("mine Token Refresh", tokenRefresh);
            updateToken(tokenRefresh, myId);
        }



    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_account:
                Intent intent = new Intent(MainActivity.this, EditUserActivity.class);
                startActivity(intent);
                break;
            case R.id.action_change_password:
                gantiPassword();
                break;
            case R.id.action_change_token:
                ambilTokenRegister();
                break;
            case R.id.action_logout:
                onLogout();
                break;
            default:
                break;
        }

        return true;
    }

    private void onLogout() {

        ConnectivityManager cm = (ConnectivityManager) MainActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Loging Out App");
        alertDialog.setMessage("Are you sure want to logout?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();

                if (isConnected) {
                    updateLoginGET(myId);

                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    db.execSQL("delete from entry where _ID = '" + myId + "'");

                    Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
                    toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    toLogin.putExtra("isLogedout", true);
                    finish();
                    startActivity(toLogin);
                }
                if (!isConnected) {
                    Toast.makeText(MainActivity.this, "Harap Logout ketika sambungan internet tersedia", Toast.LENGTH_LONG).show();
                }
            }
        });

        alertDialog.show();

    }

    private void gantiPassword() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.ubah_password, null);
        dialogBuilder.setView(dialogView);

        final EditText editText = (EditText) dialogView.findViewById(R.id.new_pass);
        final EditText editText1 = (EditText) dialogView.findViewById(R.id.confirm_pass);

        final String[] myData = dataUser.split("&&");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setTitle("Ubah password");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ubah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String newPassword = editText.getText().toString();
                String confirmPass = editText1.getText().toString();

                if(newPassword.equals("") || confirmPass.equals("")){
                    Toast.makeText(MainActivity.this, "Harap isi semua field", Toast.LENGTH_LONG).show();
                } else if (!newPassword.equals(confirmPass)) {
                    Toast.makeText(MainActivity.this, "Password tidak sama", Toast.LENGTH_LONG).show();
                } else {
                    pbMain.setVisibility(View.VISIBLE);
                    gantiPasswordGET(myData[5], newPassword);
                    alertDialog.dismiss();
                }

            }
        });
        alertDialog.show();
    }

    private void gantiTokenRegister() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_token, null);
        dialogBuilder.setView(dialogView);

        final Spinner spGantiToken = dialogView.findViewById(R.id.ct_sp_usergroup);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.user,
                android.R.layout.simple_spinner_dropdown_item);
        spGantiToken.setAdapter(arrayAdapter);

        final EditText etToken = dialogView.findViewById(R.id.ct_et_token);
        final TextView user = dialogView.findViewById(R.id.ct_txt_username);
        final TextView tgl = dialogView.findViewById(R.id.ct_txt_tgl);
        final String[] name = dataUser.split("&&");
        spGantiToken.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String pilihanUser = String.valueOf(i + 1);
                for(int j=0; j<JSONParser.tokenItems.size(); j++){
                    if(JSONParser.tokenItems.get(j).idToken.equals(pilihanUser)){
                        etToken.setText(JSONParser.tokenItems.get(j).token);
                        user.setText(JSONParser.tokenItems.get(j).username);
                        tgl.setText(JSONParser.tokenItems.get(j).tglUpdate);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ubah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = name[2];
                String token = etToken.getText().toString();
                String id = String.valueOf(spGantiToken.getSelectedItemId()+1);

                pbMain.setVisibility(View.VISIBLE);
                ubahTokenRegister(token, id, username);
            }
        });
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemReselectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_report:
                    toolbar.setTitle("Report");
                    fragment = new ReportFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_notif:
                    toolbar.setTitle("Notification");
                    fragment = new NotifFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_users:
                    toolbar.setTitle("Users");
                    fragment = new UsersFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }

    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    public void gantiPasswordGET(final String email, String password) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/resetpass?email=" + email + "&password=" + password;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbMain.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };
        queue.add(stringRequest);
    }

    private void updateLoginGET(String id) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/smallupdate?" + "id=" + id + "&identifier=" + "login" +
                "&status=0";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                Log.e("update status login", response);
            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }
        };
        queue.add(stringRequest);
    }

    private void updateToken(final String token, final String id) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/smallupdate";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
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

                params.put("id", id);
                params.put("token", token);

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

    private void ambilTokenRegister() {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/base/tb_token";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                long token = 0;
                if (response.contains("ok")) {
                    new JSONParser.ParseJson(response, token);
                    gantiTokenRegister();
                } else {
                    Toast.makeText(MainActivity.this, "Saat ini, token belum bisa diubah", Toast.LENGTH_SHORT).show();
                }
            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }
        };
        queue.add(stringRequest);
    }

    private void ubahTokenRegister(final String token, final String id, final String username) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/smallupdate?id="+id+"&token="+token+"&user_update="
                        + username+"&identifier=token";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
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

        };

        queue.add(stringRequest);
    }



    Response.ErrorListener errorListener = new
            Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof NetworkError) {
                        pbMain.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "No network available", Toast.LENGTH_LONG).show();
                    } else {
                        pbMain.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };
}
