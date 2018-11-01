package id.co.gets.myreport.main;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import id.co.gets.myreport.BuildConfig;
import id.co.gets.myreport.R;
import id.co.gets.myreport.network.SingletonRequestQueue;

public class DaftarActivity extends AppCompatActivity {

    ProgressBar pbDaftar;
    String grup, ambilToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);


        pbDaftar = findViewById(R.id.pb_daftar);
        pbDaftar.setVisibility(View.INVISIBLE);

        Spinner spGrup = findViewById(R.id.reg_sp_usergroup);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.group
                , android.R.layout.simple_spinner_dropdown_item);
        spGrup.setAdapter(arrayAdapter);

        final TableRow trRegion = findViewById(R.id.row_region);
        final TableRow trLkc = findViewById(R.id.row_lkc);
        final TableRow trVendor = findViewById(R.id.row_vendor);
        final TableRow trKota = findViewById(R.id.row_kota);
        trRegion.setVisibility(View.GONE);
        trLkc.setVisibility(View.GONE);
        trVendor.setVisibility(View.VISIBLE);
        trKota.setVisibility(View.VISIBLE);

        ambilToken = "";
        ambilToken();

        final AutoCompleteTextView acRegion = findViewById(R.id.reg_ac_region);
        ArrayAdapter<CharSequence> arrayAdapter2 = ArrayAdapter.createFromResource(this, R.array.region
                , android.R.layout.simple_dropdown_item_1line);
        acRegion.setAdapter(arrayAdapter2);

        spGrup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String namaGrup = parent.getItemAtPosition(position).toString();

                if(namaGrup.equals("Vendor")) {
                    grup = "1";
                    trRegion.setVisibility(View.GONE);
                    trLkc.setVisibility(View.GONE);
                    trVendor.setVisibility(View.VISIBLE);
                    trKota.setVisibility(View.VISIBLE);
                } else if(namaGrup.equals("CMS")) {
                    grup = "2";
                    trRegion.setVisibility(View.VISIBLE);
                    trLkc.setVisibility(View.VISIBLE);
                    trVendor.setVisibility(View.GONE);
                    trKota.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView btnDaftar = findViewById(R.id.reg_btn_daftar);
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getUsername = ((EditText)findViewById(R.id.reg_et_username)).getText().toString();
                String getName = ((EditText)findViewById(R.id.reg_et_nama)).getText().toString();
                String getEmail = ((EditText)findViewById(R.id.reg_et_email)).getText().toString();
                String getPassword = ((EditText)findViewById(R.id.reg_et_password)).getText().toString();
                String getConf = ((EditText)findViewById(R.id.reg_et_conf)).getText().toString();
                String getToken = ((EditText)findViewById(R.id.reg_et_token)).getText().toString();

                Log.e("Ambil Token", ambilToken);

                if (ambilToken.equals("")) {
                    Toast.makeText(DaftarActivity.this, "Registrasi belum dapat dilakukan, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                } else if(grup.equals("2")) {
                    EditText editText = (EditText)findViewById(R.id.reg_et_KCI);
                    EditText editText2 = (EditText)findViewById(R.id.reg_et_KCP);

                    String region = acRegion.getText().toString();
                    String kci = editText.getText().toString();
                    String kcp = editText2.getText().toString();

                   if(getUsername.equals("") || getName.equals("") || getEmail.equals("") ||
                            getPassword.equals("") || getConf.equals("") || getToken.equals("") || region.equals("")
                            || kci.equals("") || kcp.equals("")){
                        Toast.makeText(DaftarActivity.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                    } else if(!getConf.equals(getPassword)) {
                        Toast.makeText(DaftarActivity.this, "Confirm password tidak sama", Toast.LENGTH_SHORT).show();
                    } else if(!getToken.equals(ambilToken)){
                        Toast.makeText(DaftarActivity.this, "Token Anda salah", Toast.LENGTH_SHORT).show();
                    } else if(getConf.equals(getPassword) && !getConf.equals("") && !getPassword.equals("") && getToken.equals("MAYREGEND")) {
                        pbDaftar.setVisibility(View.VISIBLE);
                        registerPOST(grup, getUsername, getName, getEmail, getPassword, kcp, kci, region);
                    }

                } else if(grup.equals("1")) {

                    EditText editText3 = (EditText)findViewById(R.id.reg_et_vendor);
                    EditText editText4 = (EditText)findViewById(R.id.reg_et_kota) ;

                    String getVendor = editText3.getText().toString();
                    String getKota = editText4.getText().toString();

                    if(getUsername.equals("") || getName.equals("") || getEmail.equals("") || getKota.equals("") ||
                            getPassword.equals("") || getConf.equals("") || getToken.equals("") || getVendor.equals("") ){
                        Toast.makeText(DaftarActivity.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                    } else if(!getConf.equals(getPassword)) {
                        Toast.makeText(DaftarActivity.this, "Confirm password tidak sama", Toast.LENGTH_SHORT).show();
                    } else if(!getToken.equals("MAYREGEND")){
                        Toast.makeText(DaftarActivity.this, "Token Anda salah", Toast.LENGTH_SHORT).show();
                    } else if(getConf.equals(getPassword) && !getConf.equals("") && !getPassword.equals("") && getToken.equals("MAYREGEND")) {
                        pbDaftar.setVisibility(View.VISIBLE);
                        registerPOST(grup, getUsername, getName, getEmail, getPassword, getVendor, "", getKota);
                    }
                }
            }
        });
    }

    private void registerPOST(final String group, final String username, final String name, final String email,
                              final String password, final String vendor, final String alamat, final String kota) {
        String finalePrint = group + username + name + email + password + vendor + alamat + kota;
        Log.e("Daftar user", finalePrint);

        RequestQueue queue = SingletonRequestQueue.getInstance(DaftarActivity.this).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/authend/tb_users";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbDaftar.setVisibility(View.INVISIBLE);
                Toast.makeText(DaftarActivity.this, response, Toast.LENGTH_LONG).show();
            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }

            @Override
            public Map getParams() {
                Map params = new HashMap();

                params.put("nama_vendor", vendor);
                params.put("nama_lengkap", name);
                params.put("email", email);
                params.put("kota", kota);
                params.put("alamat", alamat);
                params.put("grup", group);
                params.put("password", password);

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

    private void ambilToken() {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/smallupdate?id=2&identifier=gettoken";

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
                        pbDaftar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "No network available", Toast.LENGTH_LONG).show();
                    } else {
                        pbDaftar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };

}
