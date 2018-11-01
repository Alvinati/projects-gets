package id.gets.bookingservice;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;


public class HistoryFragment extends Fragment{

    private View rootView;
    ListView listHistory;
    ProgressBar progressBar;
    String[] data;
    String id, savedHistory;
    DbHelper mDbHelper;
    protected Cursor cursor;
    SwipeRefreshLayout main;
    ContentValues values = new ContentValues();

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_history, container, false);
        }

        mDbHelper = new DbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM entry", null);
        cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    id = cursor.getString(0);
                } while (cursor.moveToNext());
            }


        listHistory = (ListView)rootView.findViewById(R.id.listView1);
        progressBar = (ProgressBar)rootView.findViewById(R.id.pb_history);
        main = (SwipeRefreshLayout) rootView.findViewById(R.id.his_lay_connected);
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.his_linear_list);
        TextView textKeterangan = (TextView)rootView.findViewById(R.id.his_keterangan);

        ConnectivityManager cm = (ConnectivityManager)getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Log.e("isConnected his", String.valueOf(isConnected));

        if(isConnected){
            textKeterangan.setText("History Booking");
            layout.setVisibility(View.VISIBLE);
            makeSearchQuery(id);

        } else {
            layout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            textKeterangan.setText("No network available");
        }

        main.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnected){
                    makeSearchQuery(id);
                }
            }
        });

        listHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView <? > arg0, View v, int pos,
                                    long id) {

                Intent myintent = new Intent(getActivity(), ReportActivity.class);
                ListContent that = (ListContent)listHistory.getItemAtPosition(pos);
                String[] data = {that.getMtanggalBook(), that.getmServis(), that.getmMobil(),
                        that.getmUid(), that.getmIduser(), that.getmStatus(), that.getmHargaServis()};
                myintent.putExtra("Data", data);
                startActivity(myintent);
            }

        });


        return rootView;

    }


    //----------------URL CONNECTION--------------------------------------------------------------

    private void makeSearchQuery(String id){

        URL getServisURL = NetworksUtil.buildUrlHis(id);
        new HistoryFragment.QueryTask().execute(getServisURL);

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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;

        }

        @Override
        protected void onPostExecute(String SearchResults) {
            new ContentCreator.ParseJson(SearchResults);

            progressBar.setVisibility(View.INVISIBLE);
            if(ContentCreator.history != null) {
                ListAdapter adapter = new ListAdapter(getActivity(), ContentCreator.history,
                        R.layout.list_report_content);
                listHistory.setAdapter(adapter);
            }
            main.setRefreshing(false);
        }

    }


}
