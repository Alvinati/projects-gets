package id.gets.bookingservice;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.DefaultSliderView;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.glide.slider.library.Tricks.ViewPagerEx;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener{

    private View rootView;
    private String selectedDate;
    TextView jamPilihan;
    Spinner spinner;
    private String selectedTime;
    private SliderLayout mSlider;
    TextSliderView sliderView;
    protected Cursor cursor;
    String id, hasil;
    ContentValues values = new ContentValues();


   // int[] sampleImages = {R.drawable.promo, R.drawable.promo1, R.drawable.promo3};

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }
//
//        carouselView = (CarouselView) rootView.findViewById(R.id.promoView);
//        carouselView.setPageCount(sampleImages.length);
//        carouselView.setImageListener(imageListener);
        ImageView pengganti = rootView.findViewById(R.id.image_pengganti);
        mSlider = rootView.findViewById(R.id.slider);

        ConnectivityManager cm = (ConnectivityManager)getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Log.e("isConnected his", String.valueOf(isConnected));

        DbHelper mDbHelper = new DbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM entry", null);
        String hasil = "";
        String myUser= "";
        if(cursor.moveToFirst()){
            do {
                id = cursor.getString(0);
                hasil = cursor.getString(cursor.getColumnIndex("promo"));
                myUser = cursor.getString(cursor.getColumnIndex("user"));
            } while (cursor.moveToNext());

            Log.e("myuserinhome", myUser);
        }

        if(isConnected && hasil == null){
            promoGET();
            Log.e("searchdulu", "ya");
            pengganti.setVisibility(View.GONE);
        } else if(!isConnected || !hasil.equals("")){
            pengganti.setVisibility(View.GONE);
            Log.e("ambil db", "ya");
            mSlider.removeAllSliders();
            new ContentCreator.ParseJsonPromo(hasil);
            setContentPromo();
        } else {
            mSlider.setVisibility(View.GONE);
            pengganti.setVisibility(View.VISIBLE);
            pengganti.setImageResource(R.drawable.logo_hd);
        }


        CalendarView calendarView = (CalendarView) rootView.findViewById(R.id.calendarView);

        //set current date (needed when user not change selected date)
        long milliseconds = calendarView.getDate();
        selectedDate = getDate(milliseconds, "yyyy-MM-dd");
        Log.e("dari milisec", selectedDate);
        calendarView.setMinDate(milliseconds);

        //get date value on selected date
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int date) {
                selectedDate =String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(date);
                Log.e("pilih tanggal", selectedDate);
            }
        });

        jamPilihan = (TextView) rootView.findViewById(R.id.waktu_pilihan);
        spinner = (Spinner)rootView.findViewById(R.id.pilih_jam);
        selectedTime = spinner.getSelectedItem().toString();

        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.jam, R.layout.spinner_item
        );
        spinner.setAdapter(adapter);


        Button next1 = (Button) rootView.findViewById(R.id.next1);
        next1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(selectedTime == null){
                    Toast.makeText(getActivity(), "Harap pilih waktu booking", Toast.LENGTH_SHORT).show();
                } else {
                    selectedTime = spinner.getSelectedItem().toString();
                    Log.e("pencet pilih", selectedDate);
                    Log.e("pilih waktu", String.valueOf(selectedTime));

                    Intent pilihServis = new Intent(getActivity(), PilihActivity.class);
                    pilihServis.putExtra("SELECTED_DATE", selectedDate);
                    pilihServis.putExtra("SELECTED_TIME", selectedTime);
                    startActivity(pilihServis);
                }
            }
        });

        return rootView;
    }

    private static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onPause() {
        super.onPause();
        mSlider.stopAutoCycle();

    }

    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getActivity(), slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    //----------------URL CONNECTION--------------------------------------------------------------

    private void makeSearchQuery(){

        URL getServisURL = NetworksUtil.buildUrlPromo();
        new HomeFragment.QueryTask().execute(getServisURL);

    }

    private class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(URL... urls) {

            URL searchUrl = urls[0];
            String results = null;

            try {
                results = NetworksUtil.getResponseFromHttpUrl(searchUrl);
                Log.e("PROMO RES", results);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;

        }

        @Override
        protected void onPostExecute(String SearchResults) {
            new ContentCreator.ParseJsonPromo(SearchResults);

            DbHelper mDbHelper = new DbHelper(getContext());
            // Gets the data repository in write mode
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            // Create a new map of values, where column names are the keys
            values.put(FeedReaderContract.FeedEntry.COLUMN_PROMO, SearchResults);
            db.update(FeedReaderContract.FeedEntry.TABLE_NAME, values,"_ID = ?", new String[]{id});

        }

    }

    private void promoGET(){
        RequestQueue queue = SingletonRequestQueue.getInstance(getContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = NetworksUtil.BS_PROMO_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");

                new ContentCreator.ParseJsonPromo(response);
                setContentPromo();

                DbHelper mDbHelper = new DbHelper(getContext());
                // Gets the data repository in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                // Create a new map of values, where column names are the keys
                values.put(FeedReaderContract.FeedEntry.COLUMN_PROMO, response);
                db.update(FeedReaderContract.FeedEntry.TABLE_NAME, values,"_ID = ?", new String[]{id});
            }
        }, errorListener){
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);

    }

    Response.ErrorListener errorListener = new
            Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof NetworkError) {
                        Toast.makeText(getContext(), "No network available", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };

    private void setContentPromo() {
        ArrayList<String> listUrl = ContentCreator.gambarPromo;
        ArrayList<String> listName = ContentCreator.namaPromo;

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();

        for (int i = 0; i < listUrl.size(); i++) {
            sliderView = new TextSliderView(getContext());
            // if you want show image only / without description text use DefaultSliderView instead
            // initialize SliderLayout
            sliderView
                    .image(NetworksUtil.BASE_URL + "assets/images/" + listUrl.get(i))
                    .description(listName.get(i))
                    .setRequestOption(requestOptions)
                    .setBackgroundColor(Color.WHITE)
                    .setProgressBarVisible(true)
                    .setOnSliderClickListener(this);

            //add your extra information
            sliderView.bundle(new Bundle());
            sliderView.getBundle().putString("extra", listName.get(i));
            mSlider.addSlider(sliderView);
        }

        mSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(3000);
        mSlider.addOnPageChangeListener(this);
    }



}
