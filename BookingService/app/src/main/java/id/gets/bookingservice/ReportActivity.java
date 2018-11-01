package id.gets.bookingservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReportActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ListView checkList;
    String[] data;
    ArrayList<ListContent> kSparepart;
    TableLayout tableLayout;
    TextView totalBiaya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        LinearLayout layTolak = (LinearLayout)findViewById(R.id.report_ditolak);
        LinearLayout layCheckList = (LinearLayout)findViewById(R.id.report_card);
        tableLayout =(TableLayout) findViewById(R.id.list_sparepart);

        Intent i = getIntent();
        data = i.getExtras().getStringArray("Data");

        TextView tanggal = (TextView)findViewById(R.id.txt_r_tanggal);
        TextView servis = (TextView)findViewById(R.id.txt_r_servis);
        TextView mobil = (TextView)findViewById(R.id.txt_r_mobil);
        TextView hargaServis = (TextView)findViewById(R.id.rpt_txt_harga_servis);
        totalBiaya = (TextView)findViewById(R.id.rpt_txt_total_biaya);
        TextView nServis = (TextView)findViewById(R.id.rpt_txt_nama_servis);


        tanggal.setText(data[0]);
        servis.setText(data[1]);
        mobil.setText(data[2]);
        hargaServis.setText(data[6]);
        nServis.setText(data[1]);

        progressBar = (ProgressBar)findViewById(R.id.pb_report);

        if(data[5].equals("DECLINED")) {
            progressBar.setVisibility(View.INVISIBLE);
            layTolak.setVisibility(LinearLayout.VISIBLE);
            layCheckList.setVisibility(LinearLayout.GONE);
        } else {
            layTolak.setVisibility(LinearLayout.GONE);
            checkList = (ListView)findViewById(R.id.listView1);
            makeSearchQuery(data[3]);
            layCheckList.setVisibility(LinearLayout.VISIBLE);
        }


    }

    private void loadDataTable(String n, String j, String h){
        final TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(0,
                            TableRow.LayoutParams.WRAP_CONTENT, 2f));
        tv.setGravity(Gravity.LEFT);
        tv.setText(n);


        final TextView tv2 = new TextView(this);
        tv2.setLayoutParams(new TableRow.LayoutParams(0,
                                TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tv2.setText(j);


        final TextView tv3 = new TextView(this);
        tv3.setLayoutParams(new TableRow.LayoutParams(0,
                                TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tv3.setText(h);


        final TableRow tr = new TableRow(this);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(17, 0, 10, 0);
        tr.setLayoutParams(layoutParams);
        tr.setBackgroundColor(getResources().getColor(R.color.colorNeutral));

        tr.addView(tv); tr.addView(tv2); tr.addView(tv3);
        tableLayout.addView(tr);

    }

    //----------------URL CONNECTION--------------------------------------------------------------

    private void makeSearchQuery(String uid){

        URL getServisURL = NetworksUtil.buildUrlRep(uid);
        new ReportActivity.QueryTask().execute(getServisURL);

    }

    private class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(URL... urls) {

            URL searchUrl = urls[0];
            String results = null;

            try {
                results = NetworksUtil.getResponseFromHttpUrl(searchUrl);
                Log.e("cb&sb", results);
                new ContentCreator.ParseJsonRep(results);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;

        }

        @Override
        protected void onPostExecute(String SearchResults) {
            progressBar.setVisibility(View.INVISIBLE);
            ListAdapter adapter = new ListAdapter(ReportActivity.this, ContentCreator.checklist,
                    R.layout.check_list_content );
            checkList.setAdapter(adapter);
            kSparepart = ContentCreator.sparepart;

            int totalSp = 0;
            if(kSparepart != null){
                for(ListContent l: kSparepart){
                    String n = l.getMtanggalBook();
                    String j = l.getmServis();
                    String h = l.getmMobil();
                    loadDataTable(n, j, h);
                    totalSp = totalSp + Integer.valueOf(h);
                }
                int total = Integer.valueOf(data[6]) + totalSp;
                totalBiaya.setText(String.valueOf(total));
            } else {
                totalBiaya.setText(data[6]);
            }

        }

    }
}
