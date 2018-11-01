package id.co.mine.myreportadmin.table;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import id.co.mine.myreportadmin.BuildConfig;
import id.co.mine.myreportadmin.R;
import id.co.mine.myreportadmin.dfactory.JSONParser;
import id.co.mine.myreportadmin.dfactory.SingletonRequestQueue;

public class TkoActivity extends AppCompatActivity {

    private Spinner spBulan;
    private List<JSONParser.TkoItem> items;
    ProgressBar pbTko;
    EditText etTahun;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tko);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle("TKO");

        spBulan = findViewById(R.id.tko_spin_bulan);
        pbTko = findViewById(R.id.pb_tko);
        pbTko.bringToFront();
        etTahun = findViewById(R.id.tko_et_tahun);
        tableLayout = findViewById(R.id.list_tko);
        etTahun.setText("2018");
        ImageButton download = findViewById(R.id.tko_download);

        tableLayout.removeAllViews();
        tableLayout.removeAllViewsInLayout();

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(TkoActivity.this, R.array.bulan,
                android.R.layout.simple_spinner_dropdown_item);
        spBulan.setAdapter(arrayAdapter);

        spBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tableLayout.removeAllViews();
                tableLayout.removeAllViewsInLayout();
                TkoGET(String.valueOf(position), etTahun.getText().toString() );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = BuildConfig.BASE_URL + "/graph/export_tko?bulan="+ spBulan.getSelectedItemPosition()+
                        "&tahun="+etTahun.getText().toString();
                Intent i = new Intent();
                i.setPackage("com.android.chrome");
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void loadDataTable(String no, final String id, String vendor, String shift, String nonShift, String tanggal,
                               String jam, String week){


        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        Date convertedDateCompare = new Date();
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(jam);
            convertedDateCompare = dateFormat.parse("10:00:00");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 0.3f));
        tv.setGravity(Gravity.LEFT);
        tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        tv.setText(no);

        final TextView tv2 = new TextView(this);
        tv2.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 0.8f));
        tv2.setText(tanggal);
        tv2.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);


        final TextView tv3 = new TextView(this);
        tv3.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1.2f));
        tv3.setText(vendor);
        tv3.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);


        final TextView tv4 = new TextView(this);
        tv4.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 0.3f));
        tv4.setText(week);
        tv4.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);


        final TextView tv5 = new TextView(this);
        tv5.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 0.8f));
        tv5.setText(nonShift);
        tv5.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);


        final TextView tv6 = new TextView(this);
        tv6.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 0.8f));
        tv6.setText(shift);
        tv6.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        if(convertedDate.after(convertedDateCompare)){
            tv.setTextColor(Color.RED);
            tv2.setTextColor(Color.RED);
            tv3.setTextColor(Color.RED);
            tv4.setTextColor(Color.RED);
            tv5.setTextColor(Color.RED);
            tv6.setTextColor(Color.RED);
        }

        final TableRow tr = new TableRow(this);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(17, 0, 10, 0);
        tr.setLayoutParams(layoutParams);
        tr.setBackgroundColor(getResources().getColor(R.color.colorNeutral));

        tr.addView(tv); tr.addView(tv2); tr.addView(tv3); tr.addView(tv4); tr.addView(tv5); tr.addView(tv6);
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

    private void TkoGET(String bulan, String tahun){
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/tkoall?bulan="+bulan+"&tahun="+tahun;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbTko.setVisibility(View.INVISIBLE);
                pbTko.bringToFront();

                new JSONParser.ParseJson(response, 0, "tko");
                items = JSONParser.tkoItems;
                int no = 1;
                for (JSONParser.TkoItem d: items) {

                    loadDataTable(String.valueOf(no++), d.idTko, d.namaVendor, d.shift, d.nonshift, d.tglRecord, d.jamRecord, d.week);
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
