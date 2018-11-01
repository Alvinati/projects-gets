package id.co.mine.myreportadmin.table;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
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

public class ApelActivity extends AppCompatActivity {

    private Spinner spBulan;
    private List<JSONParser.ApelItem> items;
    ProgressBar pbApel;
    EditText etTahun;
    TableLayout tableLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apel);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle("Apel Jaring");

        spBulan = findViewById(R.id.apel_spin_bulan);
       pbApel = findViewById(R.id.pb_apel);
       pbApel.bringToFront();
       etTahun = findViewById(R.id.apel_et_tahun);
       tableLayout = findViewById(R.id.list_apel);
       etTahun.setText("2018");
       ImageButton download = findViewById(R.id.apel_download);

        tableLayout.removeAllViews();
        tableLayout.removeAllViewsInLayout();

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(ApelActivity.this, R.array.bulan,
                android.R.layout.simple_spinner_dropdown_item);
        spBulan.setAdapter(arrayAdapter);

       spBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               tableLayout.removeAllViews();
               tableLayout.removeAllViewsInLayout();
               ApelGET(String.valueOf(position), etTahun.getText().toString() );
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });

       download.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String url = BuildConfig.BASE_URL + "/graph/export_apel?bulan="+ spBulan.getSelectedItemPosition()+
                       "&tahun="+etTahun.getText().toString();
               Intent i = new Intent();
               i.setPackage("com.android.chrome");
               i.setAction(Intent.ACTION_VIEW);
               i.setData(Uri.parse(url));
               startActivity(i);

           }
       });
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

    private void loadDataTable(String no, final String id, String vendor, String status, String tanggal, String jam){


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
                TableRow.LayoutParams.WRAP_CONTENT, 1.2f));
        tv2.setText(vendor);
        tv2.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);


        if(status.equals("1")){
            status = "Tidak Lengkap";
        } else if(status.equals("2")) {
            status = "Belum Lengkap";
        } else if(status.equals("3")) {
            status = "Lengkap";
        }

        final TextView tv3 = new TextView(this);
        tv3.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 0.8f));
        tv3.setText(status);
        tv3.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);


        final TextView tv4 = new TextView(this);
        tv4.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 0.8f));
        tv4.setText(tanggal);
        tv4.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);


        final TextView tv5 = new TextView(this);
        tv5.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
        tv5.setText(jam);
        tv5.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);


        if(convertedDate.after(convertedDateCompare)){
            tv.setTextColor(Color.RED);
            tv2.setTextColor(Color.RED);
            tv3.setTextColor(Color.RED);
            tv4.setTextColor(Color.RED);
            tv5.setTextColor(Color.RED);
        }

        final TableRow tr = new TableRow(this);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(17, 0, 10, 0);
        tr.setLayoutParams(layoutParams);
        tr.setBackgroundColor(getResources().getColor(R.color.colorNeutral));

        tr.addView(tv); tr.addView(tv2); tr.addView(tv3); tr.addView(tv4); tr.addView(tv5);
        tableLayout.addView(tr);

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

    private void ApelGET(String bulan, String tahun){
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/apelall?bulan="+bulan+"&tahun="+tahun;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbApel.setVisibility(View.INVISIBLE);

                new JSONParser.ParseJson(response, 0);
                items = JSONParser.apelItems;
                int no = 1;
                for (JSONParser.ApelItem d: items) {
                    loadDataTable(String.valueOf(no++), d.idApel, d.namaVendor, d.status, d.tglRecord, d.jamRecord);
                }
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
