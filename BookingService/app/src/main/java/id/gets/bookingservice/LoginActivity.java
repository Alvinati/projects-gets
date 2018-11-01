package id.gets.bookingservice;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.io.IOException;
import java.net.URL;



public class LoginActivity extends AppCompatActivity {

    SharedPreferences.Editor editor;
    EditText et1, et2;
    ContentValues values = new ContentValues();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        TextView textView = (TextView)findViewById(R.id.login_daftar);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(toRegister);
            }
        });

        et1 = (EditText)findViewById(R.id.login_et_email);
        et2 = (EditText)findViewById(R.id.login_et_password);
        progressBar = (ProgressBar)findViewById(R.id.loginBar);
        progressBar.setVisibility(View.INVISIBLE);

        Button loginBtn = (Button)findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String email = et1.getText().toString();
               String password = et2.getText().toString();

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
                    progressBar.setVisibility(View.VISIBLE);
                    LoginRequest(email, password);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        final AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setTitle("Exit App");
        alertDialog.setMessage("Are you sure want to exit?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                ActivityCompat.finishAffinity(LoginActivity.this);
            }
        });

        alertDialog.show();
    }


    //----------------URL CONNECTION--------------------------------------------------------------

    private void LoginRequest(String email, String password) {
        VolleyLog.DEBUG = true;
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        String uri = NetworksUtil.BS_AUTH_URL + "email=" + email + "&password=" + password;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {

                VolleyLog.wtf(response, "utf-8");

                new ContentCreator.ParseJsonAuth(response);
                String[] data = ContentCreator.dataLogin;
                progressBar.setVisibility(View.INVISIBLE);

                if(response == null){
                    Toast.makeText(LoginActivity.this, "Login Gagal", Toast.LENGTH_SHORT).show();
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < data.length; j++) {
                        sb.append(data[j]).append("-");
                    }

                    if (data[5].equals("Ok")) {

                        editor.putBoolean("isLogedin", true);
                        editor.commit();

                        Intent logedIn = new Intent(LoginActivity.this, MainActivity.class);
                        logedIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        logedIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        DbHelper mDbHelper = new DbHelper(getApplicationContext());
                        // Gets the data repository in write mode
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();
                        // Create a new map of values, where column names are the keys
                        values.put(FeedReaderContract.FeedEntry._ID, data[0]);
                        values.put(FeedReaderContract.FeedEntry.COLUMN_USER, sb.toString());
                        db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);

                        startActivity(logedIn);
                        finish();
                    } else {
                        //kalau ada error login
                        Toast.makeText(LoginActivity.this, data[0], Toast.LENGTH_SHORT).show();
                    }
                }

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

    Response.ErrorListener errorListener = new
            Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof NetworkError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "No network available", Toast.LENGTH_LONG).show();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };
}
