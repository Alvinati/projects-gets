package id.gets.bookingservice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PilihActivity extends AppCompatActivity {

    Spinner spinner, spinner2, spinner3;
    int posSpin1, posSpin2, posSpin3;
    String pilihan_jasa, pilihan_mobil, pilihan_sparepart, hargaServis, servis;
    String selectedDate, selectedTime, pilihan_tanggal, pilihan_waktu;
    String results = null;
    String savedResult = null;
    ProgressBar progressBar;
    ListView listView;
    ListAdapter adapter;
    ArrayList<ListContent> contents = new ArrayList<ListContent>();
    String[] dataServis = new String[3];
    EditText noPlat, jumlah;
    TextView biayaServis;

    List<String> pilihServis = new ArrayList<String>();
    List<String> pilihSparepart = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih);

        if( savedInstanceState != null){

        }

        SharedPreferences sharedPref = PilihActivity.this.getPreferences(Context.MODE_PRIVATE);
        int defaultValue = 0;
        posSpin1 = sharedPref.getInt("satu", defaultValue);
        posSpin2 = sharedPref.getInt("dua", defaultValue);
        posSpin3 = sharedPref.getInt("tiga", defaultValue);
        pilihan_tanggal = sharedPref.getString("tanggal", "");
        pilihan_waktu = sharedPref.getString("jam", "");
        savedResult = sharedPref.getString("spinnerData", "");
        String nomorPlat = sharedPref.getString("noPlat", "");

        selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        selectedTime = getIntent().getStringExtra("SELECTED_TIME");

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        makeSearchQuery();

        spinner = (Spinner) findViewById(R.id.sp_pilih_jasa);
        spinner2 = (Spinner) findViewById(R.id.sp_pilih_mobil);
        spinner3 = (Spinner)findViewById(R.id.sp_tambah_sparepart);
        noPlat = (EditText)findViewById(R.id.et_pilih_plat);
        jumlah = (EditText)findViewById(R.id.et_pilih_jumlah);
        biayaServis = (TextView)findViewById(R.id.txt_plh_biayaservis);

        noPlat.setText(nomorPlat);


        contents.clear();

        listView = (ListView) findViewById(R.id.listView1);
        registerForContextMenu(listView);
        adapter = new ListAdapter(PilihActivity.this, contents, R.layout.list_item_content);
        listView.setAdapter(adapter);


        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

                String mobil = adapterView.getItemAtPosition(pos).toString();
                pilihServis.clear();
                pilihSparepart.clear();

                for (int j = 0; j < ContentCreator.servisData.size(); j++) {
                    if(ContentCreator.servisData.get(j).mobil.equals(mobil)){
                        pilihServis.add(ContentCreator.servisData.get(j).nama);
                    }
                }

                for (int j = 0; j < ContentCreator.sparepartData.size(); j++) {
                    if(ContentCreator.sparepartData.get(j).mobil.equals(mobil)){
                        pilihSparepart.add(ContentCreator.sparepartData.get(j).nama);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(PilihActivity.this,
                        R.layout.spinner_item2, pilihServis);
                spinner.setAdapter(adapter);
                spinner.setSelection(posSpin1);

                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PilihActivity.this,
                        R.layout.spinner_item2, pilihSparepart);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner3.setAdapter(adapter2);
                spinner3.setSelection(posSpin3);

                if(spinner.getSelectedItem() == null){
                    hargaServis = "";
                    biayaServis.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pilihan_mobil = spinner2.getSelectedItem().toString();
                pilihan_jasa = adapterView.getItemAtPosition(i).toString();

                for (int j = 0; j < ContentCreator.servisData.size(); j++) {
                    if(ContentCreator.servisData.get(j).mobil.equals(pilihan_mobil)
                            && ContentCreator.servisData.get(j).nama.equals(pilihan_jasa)
                            ){
                        hargaServis = ContentCreator.servisData.get(j).harga;
                    }
                }
                biayaServis.setText(hargaServis);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                hargaServis = "";
                biayaServis.setText("");
            }
        });



        Button btTambah = (Button)findViewById(R.id.bt_tambah_sparepart);
        btTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 int hargaSparepart = 0;
                 int jumlahSparepart = Integer.valueOf(jumlah.getText().toString());

                pilihan_mobil = spinner2.getSelectedItem().toString();
                if(spinner3.getSelectedItem() == null) {
                    Toast.makeText(PilihActivity.this, "Sparepart belum tersedia", Toast.LENGTH_SHORT).show();
                } else {

                    pilihan_sparepart = spinner3.getSelectedItem().toString();
                    for (int j = 0; j < ContentCreator.sparepartData.size(); j++) {
                        if(ContentCreator.sparepartData.get(j).mobil.equals(pilihan_mobil)
                                && ContentCreator.sparepartData.get(j).nama.equals(pilihan_sparepart)
                                ){
                            hargaSparepart = Integer.valueOf(ContentCreator.sparepartData.get(j).harga)*jumlahSparepart;
                        }
                    }

                    contents.add(new ListContent("",pilihan_sparepart,String.valueOf(jumlahSparepart ),String.valueOf(hargaSparepart), "", "", ""));
                    refreshList();
                }

            }
        });




        FloatingActionButton next2 = (FloatingActionButton)findViewById(R.id.next2);

        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(!isConnected){
            next2.setEnabled(false);
            next2.setBackgroundColor(Color.GRAY);
            btTambah.setEnabled(false);
            Toast.makeText(PilihActivity.this, "Tidak ada koneksi internet, cek koneksi Anda dan silahkan kembali", Toast.LENGTH_SHORT).show();
        }

        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String platMobil = noPlat.getText().toString();

                if(platMobil.equals("") ){
                    Toast.makeText(PilihActivity.this, "Harap isi nomor plat mobil Anda", Toast.LENGTH_SHORT).show();
                } else {

                    if(spinner.getSelectedItem() == null){
                        servis = "";
                    } else {
                        servis = spinner.getSelectedItem().toString();
                    }
                    String mobil = spinner2.getSelectedItem().toString();
                    dataServis[0] = mobil;
                    dataServis[1] = servis;
                    dataServis[2] = hargaServis;

                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < dataServis.length; j++) {
                        sb.append(dataServis[j]).append("&");
                    }

                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST", (Serializable) contents);
                    //create bundle untuk listcontent
                    Intent intentNext2 = new Intent(getApplicationContext(), KonfirmasiActivity.class);

                    if (selectedDate == null && selectedTime == null) {
                        intentNext2.putExtra("SELECTED_DATE", pilihan_tanggal);
                        intentNext2.putExtra("SELECTED_TIME", pilihan_waktu);
                    } else {
                        intentNext2.putExtra("SELECTED_DATE", selectedDate);
                        intentNext2.putExtra("SELECTED_TIME", selectedTime);

                    }

                    intentNext2.putExtra("DATA_SERVIS", sb.toString());
                    intentNext2.putExtra("NO_PLAT", platMobil);
                    intentNext2.putExtra("LIST_ITEMS", contents);
                    startActivity(intentNext2);
                }
            }
        });

    }

    private void refreshList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();

        int readPos1 = spinner.getSelectedItemPosition();
        int readPos2 = spinner2.getSelectedItemPosition();
        int readPos3 = spinner3.getSelectedItemPosition();
        String nomorPlat = noPlat.getText().toString();

        SharedPreferences sharedPref = PilihActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("satu", readPos1);
        editor.putInt("dua", readPos2);
        editor.putInt("tiga", readPos3);
        editor.putString("noPlat", nomorPlat);
        if(selectedDate!=null && selectedTime != null) {
            editor.putString("tanggal", selectedDate);
            editor.putString("jam", selectedTime);

        }

        if(results != null){
            editor.putString("spinnerData", results );
        }
//        //save object array list to sharedpreferences, change to json first
//        if(contents != null){
//            Gson gson = new Gson();
//            String jsonServis = gson.toJson(contents);
//            editor.putString("list_servis", jsonServis);
//        }

        editor.commit();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        ListContent that = (ListContent) listView.getItemAtPosition(info.position);
        switch (item.getItemId()) {
            case R.id.action_delete:
                contents.remove(that);
                refreshList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    //----------------URL CONNECTION--------------------------------------------------------------

    private void makeSearchQuery(){

        URL getServisURL = NetworksUtil.buildUrlPlh();
        new QueryTask().execute(getServisURL);

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
            try {
                results = NetworksUtil.getResponseFromHttpUrl(searchUrl);
                new ContentCreator.ParseJsonPlh(results);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;

        }

        @Override
        protected void onPostExecute(String SearchResults) {
            progressBar.setVisibility(View.INVISIBLE);

            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PilihActivity.this,
                    R.layout.spinner_item2, ContentCreator.hasilITEMS2);
            adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

            spinner2.setAdapter(adapter2);
            spinner2.setSelection(posSpin2);

        }

    }

}
