package id.gets.bookingservice;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KonfirmasiActivity extends AppCompatActivity {

    ArrayList<ListContent> contents;
    String[] dataku, dataServisku;
    TextView nama, email, noHp, platNo, namaMobil, jam, servis, hargaServis;
    DbHelper mDbHelper;
    protected Cursor cursor;
    TableLayout tableLayout;
    String passedTanggal;
    int hargaTotal = 0;
    String responsePost1 = "";
    String responsePost2 = "";
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi);

        mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM entry", null);
        cursor.moveToFirst();
        String myUser = "";
        if (cursor.moveToFirst()) {
            do {
                myUser = cursor.getString(cursor.getColumnIndex("user"));
            } while (cursor.moveToNext());
        }

        dataku = myUser.split("-");

        passedTanggal = getIntent().getStringExtra("SELECTED_DATE");
        String passedJam = getIntent().getStringExtra("SELECTED_TIME");
        String noPlat = getIntent().getStringExtra("NO_PLAT");
        String dataServis = getIntent().getStringExtra("DATA_SERVIS");
        contents = (ArrayList<ListContent>) getIntent().getSerializableExtra("LIST_ITEMS");

        Log.e("noPlat", noPlat);
        Log.e("dataservis", dataServis);
        Log.e("contentsKonfr", contents.toString());

         nama = (TextView)findViewById(R.id.cfm_txt_nama);
         email = (TextView)findViewById(R.id.cfm_txt_email);
         noHp = (TextView)findViewById(R.id.cfm_txt_noHp);
         platNo = (TextView)findViewById(R.id.cfm_txt_noPlat);
         namaMobil = (TextView)findViewById(R.id.cfm_txt_mobil);


        nama.setText(dataku[1]);
        email.setText(dataku[3]);
        noHp.setText(dataku[2]);
        platNo.setText(noPlat);


        //change String to date
        SimpleDateFormat format1= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = format1.parse(passedTanggal);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //change again to right format
        String dayOfTheWeek, day, monthString, year;
        dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
        day          = (String) DateFormat.format("dd",   date); // 20
        monthString  = (String) DateFormat.format("MMM",  date); // Jun
        year         = (String) DateFormat.format("yyyy", date); // 2013

        final TextView hari, tanggal, bulan, tahun;

        hari = (TextView)findViewById(R.id.cfm_txt_hari);
        tanggal = (TextView)findViewById(R.id.cfm_txt_tanggal);
        bulan = (TextView)findViewById(R.id.cfm_txt_bulan);
        tahun = (TextView)findViewById(R.id.cfm_txt_tahun);
        jam = (TextView)findViewById(R.id.cfm_txt_waktu);
        servis = (TextView)findViewById(R.id.cfm_txt_servis);
        hargaServis = (TextView)findViewById(R.id.cfm_txt_hargaS);
        tableLayout = (TableLayout)findViewById(R.id.cfm_list_sparepart);
        progressBar = (ProgressBar)findViewById(R.id.book_bar);
        TextView txtTotalHarga = (TextView)findViewById(R.id.cfm_txt_total);
        progressBar.setVisibility(View.INVISIBLE);


        dataServisku = dataServis.split("&");
        hari.setText(dayOfTheWeek);
        tanggal.setText(day);
        bulan.setText(monthString);
        tahun.setText(year);
        jam.setText(passedJam);
        namaMobil.setText(dataServisku[0]);
        if(dataServisku.length > 1){
        servis.setText(dataServisku[1]);
        hargaServis.setText(dataServisku[2]);
        hargaTotal = hargaTotal + Integer.valueOf(dataServisku[2]);
        } else {
            servis.setText("");
            hargaServis.setText("");
        }


        for(ListContent l: contents){
            String n = l.getmServis();
            String j = l.getmMobil();
            String h = l.getmUid();
            loadDataTable(n, j, h);
            hargaTotal = hargaTotal + Integer.valueOf(h);
        }

        txtTotalHarga.setText(String.valueOf(hargaTotal));

        Button ubah = (Button)findViewById(R.id.cfm_btn_ubah);
        ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEdit = new Intent(KonfirmasiActivity.this, EditPemesanActivity.class);
                toEdit.putExtra("data", dataku);
                startActivityForResult(toEdit, 1);
            }
        });

        Button book = (Button)findViewById(R.id.cfm_btn_book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);

                String uuid = UUID.randomUUID().toString();
                final String mySubstring = uuid.substring(0, 5);
                POSTRequest(mySubstring);

                delayedFinish();
            }
        });

    }

    private void delayedFinish(){

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }

    private void loadDataTable(String n, String j, String h){
        final TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 2f));
        tv.setGravity(Gravity.LEFT);
        tv.setText(n);
        tv.setTextSize(12f);


        final TextView tv2 = new TextView(this);
        tv2.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tv2.setText(j);
        tv2.setTextSize(12f);


        final TextView tv3 = new TextView(this);
        tv3.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tv3.setText(h);
        tv3.setTextSize(12f);


        final TableRow tr = new TableRow(this);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 10, 0);
        tr.setLayoutParams(layoutParams);
        tr.setBackgroundColor(getResources().getColor(R.color.colorNeutral));

        tr.addView(tv); tr.addView(tv2); tr.addView(tv3);
        tableLayout.addView(tr);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                dataku = data.getStringArrayExtra("data");
                nama.setText(dataku[1]);
                email.setText(dataku[3]);
                noHp.setText(dataku[2]);
            } else if(resultCode == 0){
                System.out.println("RESULT CANCELED");
            }
        }
    }


    private void POSTRequest(final String uuid) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;

        String uri = NetworksUtil.BASE_URL + "api/bookapi";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                Log.e("Book", response);

                if(response.contains("berhasil") && !contents.toString().equals("[]")){
                    Toast.makeText(KonfirmasiActivity.this, "Booking servis berhasil", Toast.LENGTH_LONG).show();
                    for (ListContent i: contents) {
                        String n = i.getmServis();
                        String j = i.getmMobil();
                        String h = i.getmUid();
                        POSTRequest2(uuid, n, j, h);
                    }
                }else if(responsePost1.contains("berhasil") && contents.toString().equals("[]")) {
                    Toast.makeText(KonfirmasiActivity.this, "Booking Anda berhasil, " +
                            "silahkan cek history untuk melihat status booking", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(KonfirmasiActivity.this, response, Toast.LENGTH_SHORT ).show();
                }

            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map params = new HashMap();

                params.put("uuid", uuid);
                params.put("tgl_booking", passedTanggal);
                params.put("jam_booking", jam.getText().toString());
                params.put("nama_owner", nama.getText().toString());
                params.put("kontak", noHp.getText().toString());
                params.put("no_plat", platNo.getText().toString());
                params.put("nama_servis", servis.getText().toString());
                params.put("harga_servis", hargaServis.getText().toString());
                params.put("id_user", dataku[0]);
                params.put("email_owner", email.getText().toString());
                params.put("nama_mobil", dataServisku[0]);
                params.put("total_biaya", String.valueOf(hargaTotal));

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

    private void POSTRequest2(final String uuid, final String n, final String j, final String h) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;

        String uri = NetworksUtil.BASE_URL + "api/testapi";

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                Log.e("Book", response);
                Toast.makeText(KonfirmasiActivity.this, response, Toast.LENGTH_SHORT).show();
                responsePost2 = response;
            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map params = new HashMap();

                params.put("mobil", dataServisku[0]);
                params.put("uuid", uuid);
                params.put("nama_sparepart", n);
                params.put("harga_spb", h);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                return  headers;
            }
        };

        queue.add(stringRequest2);
    }

    private void checkParams(){
        String[] params = new String[12];
        params[0] = passedTanggal;
        params[1] = jam.getText().toString();
        params[2] = nama.getText().toString();
        params[3] = noHp.getText().toString();
        params[4] = platNo.getText().toString();
        params[5] = servis.getText().toString();
        params[6] = hargaServis.getText().toString();
        params[7] = dataku[0];
        params[8] = email.getText().toString();
        params[9] = dataServisku[0];
        params[10] = String.valueOf(hargaTotal);
        String uuid = UUID.randomUUID().toString();
        String mySubstring = uuid.substring(0, 5);
        params[11] = mySubstring;

        for (String printParam: params) {
            Log.e("iniParamPost", printParam);
        }
    }

    private void checkParams2(){
        String[] params = new String[12];

        for (String printParam: params) {
            Log.e("iniParamPost", printParam);
        }
    }
}
