package id.co.gets.myreport.main;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


import id.co.gets.myreport.BuildConfig;
import id.co.gets.myreport.R;
import id.co.gets.myreport.dbhelper.DbHelper;
import id.co.gets.myreport.network.SingletonRequestQueue;

public class NewInvoiceActivity extends AppCompatActivity {

    DbHelper mDbHelper;
    String myId, namaVendor;
    String [] dataUser, dataMaybank;
    TextView btnSubmit;
    TextView noInvoice;
    String  nomorInvoice, nomTko, jmlTko, nomBpjs, jmlBpjs, nomLembur, jmlLembur, nomKoneksi, jmlKoneksi,
            nomPatroli, nomTemporary, jmlTemporary;
    ProgressBar pbNewInvoice;
    String tetapKirim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invoice);

        mDbHelper = new DbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM entry", null);
        cursor.moveToFirst();
        myId = "";
        String rawUser = "", rawMaybank = "";
        if (cursor.moveToFirst()) {
            do {
                myId = cursor.getString(0);
                namaVendor = cursor.getString(cursor.getColumnIndex("vendor"));
                rawUser = cursor.getString(cursor.getColumnIndex("user"));
                rawMaybank = cursor.getString(cursor.getColumnIndex("maybank"));
            } while (cursor.moveToNext());
        }
        cursor.close();

        dataUser = rawUser.split("&&");
        dataMaybank = rawMaybank.split("&&");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        SimpleDateFormat mdformat2 = new SimpleDateFormat("dd");
        SimpleDateFormat mdformat3 = new SimpleDateFormat("M");
        String strDate = mdformat.format(calendar.getTime());
        String strTanggal = mdformat2.format(calendar.getTime());
        String strBulan = mdformat3.format(calendar.getTime());

        String cNoInvoice = strTanggal + strBulan + myId;

        ((TextView) findViewById(R.id.new_in_name)).setText(dataUser[4]);
        ((TextView) findViewById(R.id.new_in_kota)).setText(dataUser[8]);
         noInvoice = findViewById(R.id.invoice_number);
        ((TextView) findViewById(R.id.new_in_tanggal)).setText(strDate);
        ((TextView) findViewById(R.id.new_in_co)).setText(dataMaybank[0]);
        ((TextView) findViewById(R.id.new_in_tujuan)).setText(dataMaybank[1]);
        ((TextView) findViewById(R.id.new_in_alamat)).setText(dataMaybank[3]);

         noInvoice.setText(cNoInvoice);

        final EditText etNomTko, etJmlTko, etNomLembur, etNomBpjs, etJmlBpjs, etNomPatroli,
                etNomKoneksi, etJmlKoneksi, etNomTemporary, etJmlTemporary;

        etNomTko = findViewById(R.id.nom_tko);
        etJmlTko = findViewById(R.id.jml_tko);
        etNomBpjs = findViewById(R.id.nom_bpjs);
        etJmlBpjs = findViewById(R.id.jml_bpjs);
        etNomLembur = findViewById(R.id.nom_lembur);
        etNomKoneksi = findViewById(R.id.nom_koneksi);
        etJmlKoneksi = findViewById(R.id.jml_koneksi);
        etNomPatroli = findViewById(R.id.nom_patroli);
        etNomTemporary = findViewById(R.id.nom_temporary);
        etJmlTemporary = findViewById(R.id.jml_temporary);

        pbNewInvoice = findViewById(R.id.new_in_pb);
        pbNewInvoice.setVisibility(View.INVISIBLE);

        btnSubmit = findViewById(R.id.new_in_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomTko = etNomTko.getText().toString();
                jmlTko = etJmlTko.getText().toString();
                nomBpjs = etNomBpjs.getText().toString();
                jmlBpjs = etJmlBpjs.getText().toString();
                nomLembur = etNomLembur.getText().toString();
                nomKoneksi = etNomKoneksi.getText().toString();
                jmlKoneksi = etJmlKoneksi.getText().toString();
                nomPatroli = etNomPatroli.getText().toString();
                nomTemporary = etNomTemporary.getText().toString();
                jmlTemporary = etJmlTemporary.getText().toString();
                nomorInvoice = noInvoice.getText().toString();
                tetapKirim = "no";

                pbNewInvoice.setVisibility(View.VISIBLE);
                invoicePOST();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        pbNewInvoice.setVisibility(View.INVISIBLE);

                    }
                }, 3000);


            }
        });

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

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    private void invoicePOST() {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "api/invoice";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbNewInvoice.setVisibility(View.INVISIBLE);
                if(response.contains("hari")){
                    final AlertDialog alertDialog = new AlertDialog.Builder(NewInvoiceActivity.this).create();
                    alertDialog.setTitle(response);
                    alertDialog.setMessage("Apakah yakin ingin mengirim invoice lagi?");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            pbNewInvoice.setVisibility(View.VISIBLE);
                            tetapKirim = "yes";
                            invoicePOST();
                        }
                    });
                    alertDialog.show();
                } else {
                    tetapKirim = "no";
                    Toast.makeText(NewInvoiceActivity.this, response, Toast.LENGTH_LONG).show();
                }


            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.NORMAL;
            }

            @Override
            public Map getParams() {
                Map params = new HashMap();

                params.put("no_invoice", nomorInvoice);
                params.put("nama_vendor", namaVendor);
                params.put("nom_tko", nomTko);
                params.put("jml_tko", jmlTko);
                params.put("nom_lembur", nomLembur);
                params.put("nom_bpjs", nomBpjs);
                params.put("jml_bpjs", jmlBpjs);
                params.put("patroli", nomPatroli);
                params.put("nom_koneksi", nomKoneksi);
                params.put("jml_koneksi", jmlKoneksi);
                params.put("nom_temporary", nomTemporary);
                params.put("jml_temporary", jmlTemporary);
                params.put("tetap_kirim", tetapKirim);
                params.put("sender", dataUser[5]);

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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }
}
