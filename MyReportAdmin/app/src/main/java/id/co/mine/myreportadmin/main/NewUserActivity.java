package id.co.mine.myreportadmin.main;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.co.mine.myreportadmin.BuildConfig;
import id.co.mine.myreportadmin.R;
import id.co.mine.myreportadmin.dfactory.SingletonRequestQueue;

public class NewUserActivity extends AppCompatActivity {

    ProgressBar pbRegist;
    String ambilToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        final EditText username, name, email, password, confirmPass, token;

        username = findViewById(R.id.reg_et_username);
        name = findViewById(R.id.reg_et_nama);
        email = findViewById(R.id.reg_et_email);
        password = findViewById(R.id.reg_et_password);
        confirmPass = findViewById(R.id.reg_et_conf);
        token = findViewById(R.id.reg_et_token);

        pbRegist = findViewById(R.id.pb_daftar);
        pbRegist.setVisibility(View.INVISIBLE);

        TextView btnDaftar = findViewById(R.id.btn_daftar);

        ambilToken = "";
        ambilToken();

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String getUsername = username.getText().toString();
                String getName = name.getText().toString();
                String getEmail = email.getText().toString();
                String getPassword = password.getText().toString();
                String getConf = confirmPass.getText().toString();
                String getToken = token.getText().toString();

                if (ambilToken.equals("")){
                    Toast.makeText(NewUserActivity.this, "daftar user belum dapat dilakukan, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                } else if(getUsername.equals("") || getName.equals("") || getEmail.equals("") ||
                        getPassword.equals("") || getConf.equals("") || getToken.equals("")){
                    Toast.makeText(NewUserActivity.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                } else if(!getConf.equals(getPassword)) {
                    Toast.makeText(NewUserActivity.this, "Confirm password tidak sama", Toast.LENGTH_SHORT).show();
                } else if(!getToken.equals(ambilToken)){
                    Toast.makeText(NewUserActivity.this, "Token Anda salah", Toast.LENGTH_SHORT).show();
                } else if(getConf.equals(getPassword) && !getConf.equals("") && !getPassword.equals("") && getToken.equals("MAYREG")) {
                    pbRegist.setVisibility(View.VISIBLE);
                    registerPOST(getUsername, getName, getEmail, getPassword);
                }
            }
        });
    }

    private void registerPOST(final String username, final String name, final String email, final String pass){
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;

        String uri = BuildConfig.BASE_URL + "/api/authmain/tb_muser";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbRegist.setVisibility(View.INVISIBLE);
                Toast.makeText(NewUserActivity.this, response, Toast.LENGTH_SHORT).show();
                finish();
            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map params = new HashMap();

                params.put("username", username);
                params.put("name", name );
                params.put("email", email);
                params.put("password", pass);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                return  headers;
            }
        };
        queue.add(stringRequest);
    }

    private void ambilToken() {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/smallupdate?id=1&identifier=gettoken";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                if(response.contains("ok")){
                    try {
                    JSONObject jsonObject = new JSONObject(response);
                    ambilToken = jsonObject.getString("hasil");
                    } catch (final JSONException e) {
                    Log.e("ERROR DATA INVOICE!", "Json parsing error:" +e.getMessage());
                    }
                } else {
                    ambilToken = "";
                }
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
                        pbRegist.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "No network available", Toast.LENGTH_LONG).show();
                    } else {
                        pbRegist.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };

}
