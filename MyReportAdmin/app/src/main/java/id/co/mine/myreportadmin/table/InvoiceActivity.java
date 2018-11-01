package id.co.mine.myreportadmin.table;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.util.List;

import id.co.mine.myreportadmin.BuildConfig;
import id.co.mine.myreportadmin.R;
import id.co.mine.myreportadmin.dfactory.JSONParser;
import id.co.mine.myreportadmin.dfactory.SingletonRequestQueue;

public class InvoiceActivity extends AppCompatActivity {

    Spinner spBulan;
    TableLayout tableLayout;
    private List<JSONParser.InvoiceItem> myDataset;
    ProgressBar pbInvoice;
    EditText etTahun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle("Invoice");

        etTahun = findViewById(R.id.in_et_tahun);
        etTahun.setText("2018");
        ImageButton download = findViewById(R.id.invoice_download);

        spBulan = findViewById(R.id.in_spin_bulan);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(InvoiceActivity.this, R.array.bulan,
                    android.R.layout.simple_spinner_dropdown_item);
        spBulan.setAdapter(arrayAdapter);
        spBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tableLayout.removeAllViews();
                tableLayout.removeAllViewsInLayout();
                invoiceGET(String.valueOf(position), etTahun.getText().toString() );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tableLayout = findViewById(R.id.list_invoice);
        pbInvoice = findViewById(R.id.pb_invoice);
        pbInvoice.setVisibility(View.VISIBLE);
        pbInvoice.bringToFront();

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = BuildConfig.BASE_URL + "/graph/export_invoice?bulan="+ spBulan.getSelectedItemPosition()+
                        "&tahun="+etTahun.getText().toString();
                Intent i = new Intent();
                i.setPackage("com.android.chrome");
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });



    }

    private void loadDataTable(String no, String tanggal, String vendor, String jumlah, final String id){
        final TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 0.3f));
        tv.setGravity(Gravity.LEFT);
        tv.setText(no);

        final TextView tv2 = new TextView(this);
        tv2.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 0.8f));
        tv2.setText(tanggal);
        tv2.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);


        final TextView tv3 = new TextView(this);
        tv3.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tv3.setText(vendor);
        tv3.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        final TextView tv4 = new TextView(this);
        tv4.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tv4.setText(jumlah);
        tv4.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        final ImageButton button = new ImageButton(this);
        button.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
        button.setImageResource(R.drawable.ic_search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InvoiceActivity.this, InvoiceDetailActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        final TableRow tr = new TableRow(this);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(17, 0, 10, 0);
        tr.setLayoutParams(layoutParams);
        tr.setBackgroundColor(getResources().getColor(R.color.colorNeutral));

        tr.addView(tv); tr.addView(tv2); tr.addView(tv3); tr.addView(tv4); tr.addView(button);
        tableLayout.addView(tr);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//        }
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

    private void invoiceGET(String bulan, String tahun){
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/invoiceAll?bulan="+bulan+"&tahun="+tahun;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbInvoice.setVisibility(View.INVISIBLE);

                new JSONParser.ParseJson(response, "");
                myDataset = JSONParser.invoiceItems;
                int no = 1;
                for (JSONParser.InvoiceItem d: myDataset) {
                    Integer jumlah = Integer.valueOf(d.nomBpjs) + Integer.valueOf(d.nomKoneksi) + Integer.valueOf(d.nomLembur)
                                    + Integer.valueOf(d.nomPatroli) + Integer.valueOf(d.nomTemporary) + Integer.valueOf(d.nomTko);
                    String total = String.valueOf(jumlah);

                    loadDataTable(String.valueOf(no++), d.tglInvoice, d.vendorName, total, d.idInvoice);
                }

//                if(myDataset.toString().equals("[]")){
//                    mRecyclerView.setVisibility(View.GONE);
//                    textView.setVisibility(View.VISIBLE);
//                    textView.setText(JSONParser.invoiceKosong);
//                } else if(myDataset != null && !myDataset.toString().equals("[]")) {
//                    textView.setVisibility(View.GONE);
//                    mRecyclerView.setVisibility(View.VISIBLE);
//                    mAdapter = new MyAdapter(myDataset);
//                    mRecyclerView.setAdapter(mAdapter);
//                }

            }
        }, errorListener){
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }
        };
        queue.add(stringRequest);

    }

}
