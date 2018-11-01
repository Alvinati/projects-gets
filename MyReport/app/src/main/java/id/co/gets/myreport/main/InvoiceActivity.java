package id.co.gets.myreport.main;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

import java.util.List;

import id.co.gets.myreport.BuildConfig;
import id.co.gets.myreport.R;
import id.co.gets.myreport.network.JSONParser;
import id.co.gets.myreport.network.SingletonRequestQueue;

public class InvoiceActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<JSONParser.InvoiceItem> myDataset;
    ProgressBar pbInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.invoice_toolbar);
        myToolbar.setTitle("Invoice");
        setSupportActionBar(myToolbar);

        pbInvoice = findViewById(R.id.pb_invoice);
        pbInvoice.setVisibility(View.VISIBLE);

        mRecyclerView = (RecyclerView)findViewById(R.id.invoice_recyclerview);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intCreate = new Intent(InvoiceActivity.this, NewInvoiceActivity.class);
                startActivity(intCreate);
            }
        });

        final String namaVendor = getIntent().getStringExtra("nama_vendor");

        if(namaVendor != null){
            invoiceGET(namaVendor);
        }

        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.invoice_list_view);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(namaVendor != null){
                    invoiceGET(namaVendor);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000); // Delay in millis

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


    private void invoiceGET(final String vendor){
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "api/invoice?nama_vendor=" + vendor;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbInvoice.setVisibility(View.INVISIBLE);

                new JSONParser.ParseJson(response, vendor);
                myDataset = JSONParser.invoiceItems;
                Log.e("mydataset", myDataset.toString());
                TextView textView = findViewById(R.id.invoice_kosong);
                if(myDataset.toString().equals("[]")){
                    mRecyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(JSONParser.invoiceKosong);
                } else if(myDataset != null && !myDataset.toString().equals("[]")) {
                    textView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mAdapter = new MyAdapter(myDataset);
                    mRecyclerView.setAdapter(mAdapter);
                }

            }
        }, errorListener){
            @Override
            public Priority getPriority() {
                return Priority.NORMAL;
            }
        };
        queue.add(stringRequest);

    }


}
