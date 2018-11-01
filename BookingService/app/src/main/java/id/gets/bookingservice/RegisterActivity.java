package id.gets.bookingservice;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText eEmail, eUsername, ePassword, eConfPassword;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        eUsername = (EditText)findViewById(R.id.reg_username);
        eEmail = (EditText) findViewById(R.id.reg_email);
        ePassword = (EditText)findViewById(R.id.reg_password);
        eConfPassword = (EditText)findViewById(R.id.reg_conf_pass);
        progressBar = (ProgressBar) findViewById(R.id.regis_bar);
        progressBar.setVisibility(View.INVISIBLE);


        Button register = (Button)findViewById(R.id.reg_btn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = eUsername.getText().toString();
                String email = eEmail.getText().toString();
                String password = ePassword.getText().toString();
                String confPass = eConfPassword.getText().toString();

                ConnectivityManager cm =
                        (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();


                if( username.equals("") || email.equals("") ||
                        password.equals("") || confPass.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Mohon isi semua field", Toast.LENGTH_SHORT).show();
                } else if(!password.equals(confPass)) {
                    Toast.makeText(RegisterActivity.this, "Password tidak sama", Toast.LENGTH_SHORT).show();
                } else if(!isConnected) {
                    Toast.makeText(RegisterActivity.this, "No network available", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    POSTRequest();

                }
            }
        });

    }

    private void POSTRequest() {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;

        String uri = NetworksUtil.BASE_URL + "api/authapi/tb_users";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                Log.e("RegisterResult", response);
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
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

                params.put("username", eUsername.getText().toString());
                params.put("email", eEmail.getText().toString());
                params.put("password", ePassword.getText().toString());

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

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error instanceof NetworkError) {
                Toast.makeText(getApplicationContext(), "No network available", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };


}
