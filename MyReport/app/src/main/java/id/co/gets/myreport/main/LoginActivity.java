package id.co.gets.myreport.main;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.co.gets.myreport.BuildConfig;
import id.co.gets.myreport.R;
import id.co.gets.myreport.dbhelper.DbHelper;
import id.co.gets.myreport.dbhelper.FeedReaderContract;
import id.co.gets.myreport.network.JSONParser;
import id.co.gets.myreport.network.SingletonRequestQueue;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences.Editor editor;
    Context mContext;
    Activity mActivity;
    PopupWindow mPopupWindow;
    RelativeLayout loginView;
    TextView loginBtn;
    EditText etEmail, etPassword;
    ProgressBar pbLoading;
    DbHelper mDbHelper;
    String group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = getApplicationContext();
        mActivity = LoginActivity.this;
        pbLoading = findViewById(R.id.pb_login);
        pbLoading.setVisibility(View.GONE);

        SharedPreferences sharedPref = LoginActivity.this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        mDbHelper = new DbHelper(getApplicationContext());

        Boolean isLogedout = getIntent().getBooleanExtra("isLogedout", false);
        Boolean isLogedin = sharedPref.getBoolean("isLogedin", false);
        String grupMana = sharedPref.getString("grup", "");

        if(isLogedout){
            editor.putBoolean("isLogedin", false);
            editor.apply();
        } else if(isLogedin){
            if (grupMana.equals("1")) {
                Intent directHome = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(directHome);
            } else if(grupMana.equals("2")) {
                Intent directHome2 = new Intent(LoginActivity.this, Main2Activity.class);
                startActivity(directHome2);
            }
        }

        etEmail = findViewById(R.id.login_et_email);
        etPassword = findViewById(R.id.login_et_password);

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

                Log.e("email", email);
                Log.e("password", password);
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


        Button reset = findViewById(R.id.reset_btn);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginView = findViewById(R.id.login_view);

                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View apelView = inflater.inflate(R.layout.reset_password, null);

                mPopupWindow = new PopupWindow(apelView, 600, 500);

                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }

                ImageButton closeButton = apelView.findViewById(R.id.reset_close);

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopupWindow.dismiss();
                    }
                });

                final EditText etEmail = apelView.findViewById(R.id.reset_email);

                TextView btnKirim = apelView.findViewById(R.id.reset_btn_kirim);
                btnKirim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String email = etEmail.getText().toString();

                        if(email.equals("")){
                            Toast.makeText(LoginActivity.this, "Mohon isi field", Toast.LENGTH_LONG).show();
                        } else {
                            notifPOST("[Reset Password]", email);
                        }
                    }
                });

                mPopupWindow.setFocusable(true);
                mPopupWindow.update();
                mPopupWindow.showAtLocation(loginView, Gravity.CENTER,0,0);

            }
        });

        TextView daftar = findViewById(R.id.login_btn_daftar);
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toDaftar = new Intent(LoginActivity.this, DaftarActivity.class);
                startActivity(toDaftar);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
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
        String uri = BuildConfig.BASE_URL + "api/authend/tb_users?email=" + email + "&password=" + password;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {

                VolleyLog.wtf(response, "utf-8");

                new JSONParser.ParseJson(response);

                String[] loginData = JSONParser.dataLogin;

                if(loginData[0].equals("Ok")){

                    StringBuilder sb = new StringBuilder();
                    for(int i = 0; i<loginData.length; i++){
                        sb.append(loginData[i]).append("&&");
                    }

                    ContentValues values = new ContentValues();
                    // Gets the data repository in write mode
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();

                    values.put(FeedReaderContract.FeedEntry._ID, loginData[1]);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_USER_VENDOR, loginData[3]);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_USER_DATA, sb.toString());
                    db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);

                    editor.putBoolean("isLogedin", true);
                    editor.putString("grup", loginData[9]);
                    editor.commit();

                    if(loginData[9].equals("1")) {
                        Intent logedIn = new Intent(LoginActivity.this, MainActivity.class);
                        logedIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        logedIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logedIn);
                        finish();
                    } else if(loginData[9].equals("2")) {
                        Intent logedIn2 = new Intent(LoginActivity.this, Main2Activity.class);
                        logedIn2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        logedIn2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logedIn2);
                        finish();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, loginData[1], Toast.LENGTH_SHORT).show();
                }

                pbLoading.setVisibility(View.INVISIBLE);

                Log.e("LoginResponse", response);

            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.NORMAL;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void notifPOST(final String tipe, final String email) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "api/notification";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
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
                params.put("email", email);
                params.put("tipe_pesan", tipe);
                params.put("pesan", "mohon bantuan untuk reset password user: " + email);
                params.put("status", "0");
                params.put("sender", email);

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
