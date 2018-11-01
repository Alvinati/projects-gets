package id.co.mine.myreportadmin.main;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import id.co.mine.myreportadmin.BuildConfig;
import id.co.mine.myreportadmin.R;
import id.co.mine.myreportadmin.dbhelper.DbHelper;
import id.co.mine.myreportadmin.dbhelper.FeedReaderContract;
import id.co.mine.myreportadmin.dfactory.JSONParser;
import id.co.mine.myreportadmin.dfactory.SingletonRequestQueue;
import id.co.mine.myreportadmin.services.MyFirebaseInstancedService;
import id.co.mine.myreportadmin.services.PeriodicService;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences.Editor editor;
    Context mContext;
    Activity mActivity;
    EditText etEmail, etPassword;
    ProgressBar pbLoading;
    DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = getApplicationContext();
        mActivity = LoginActivity.this;
        pbLoading = findViewById(R.id.pb_login);
        pbLoading.setVisibility(View.GONE);

        mDbHelper = new DbHelper(getApplicationContext());

        etEmail = findViewById(R.id.login_et_email);
        etPassword = findViewById(R.id.login_et_password);

        SharedPreferences sharedPref = LoginActivity.this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        Boolean isLogedout = getIntent().getBooleanExtra("isLogedout", false);
        Boolean isLogedin = sharedPref.getBoolean("isLogedin", false);

        if(isLogedout){
            editor.putBoolean("isLogedin", false);
            editor.apply();
        } else if(isLogedin){
            Intent directHome = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(directHome);
        }

        TextView buttonLogin = findViewById(R.id.btn_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                ConnectivityManager cm =
                        (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if(email.equals("") || password.equals("")){
                    Toast.makeText(LoginActivity.this, "Mohon isi email dan password", Toast.LENGTH_SHORT).show();
                } else if(!isConnected){
                    Toast.makeText(LoginActivity.this, "No Network Available", Toast.LENGTH_SHORT).show();
                }
                else {
                    pbLoading.setVisibility(View.VISIBLE);
                    LoginRequest(email, password);
                }


            }
        });

        TextView daftar = findViewById(R.id.button_daftar);
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegist = new Intent(LoginActivity.this, NewUserActivity.class);
                startActivity(toRegist);
            }
        });

    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        SharedPreferences sharedPref = LoginActivity.this.getPreferences(Context.MODE_PRIVATE);
        Boolean isLogedin = sharedPref.getBoolean("isLogedin", false);

        if(isLogedin){
            Intent directHome = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(directHome);
        }
        super.onResume();
    }

    Response.ErrorListener errorListener = new
            Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof NetworkError) {
                        pbLoading.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "No network available", Toast.LENGTH_LONG).show();
                    } else {
                        pbLoading.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };

    private void LoginRequest(String email, String password) {
        VolleyLog.DEBUG = true;
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        String uri = BuildConfig.BASE_URL + "/api/authmain/tb_muser?email=" + email + "&password="
                                        + password;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {

                VolleyLog.wtf(response, "utf-8");

                if (response.contains("hasil")) {
                    new JSONParser.ParseJson(response);

                    String[] loginData = JSONParser.dataLogin;

                    if (loginData[0].equals("Ok")) {

                        editor.putBoolean("isLogedin", true);
                        editor.commit();

                        StringBuilder sb = new StringBuilder();
                        for(int i = 0; i<loginData.length; i++){
                            sb.append(loginData[i]).append("&&");
                        }

                        ContentValues values = new ContentValues();
                        // Gets the data repository in write mode
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                        values.put(FeedReaderContract.FeedEntry._ID, loginData[1]);
                        values.put(FeedReaderContract.FeedEntry.COLUMN_MAYBANK_DATA, sb.toString());
                        db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);

                        Intent logedIn = new Intent(LoginActivity.this, MainActivity.class);
                        logedIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        logedIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logedIn);
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, loginData[1], Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login gagal, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                }
                pbLoading.setVisibility(View.INVISIBLE);
            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };
        queue.add(stringRequest);
    }


}
