package id.gets.bookingservice;


import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListAdapter extends ArrayAdapter<ListContent> {

    private int thisLayout;
    public static  final String[]passData = new String[5];

    public ListAdapter(Activity context, ArrayList<ListContent> content, int myLayout){

        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.

        super(context, 0, content);
        thisLayout = myLayout;
    }




    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    thisLayout, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        final ListContent currentContent = getItem(position);

            // Find the TextView in the list_item.xml layout with the ID version_name

        TextView tanggal = listItemView.findViewById(R.id.rpt_txt_tanggal);
        TextView bulan = listItemView.findViewById(R.id.rpt_txt_bulan);
        TextView tahun = listItemView.findViewById(R.id.rpt_txt_tahun);

            if(currentContent.getMtanggalBook() != null) {
                String getTanggal = currentContent.getMtanggalBook();

                //change String to date
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                try {
                    date = format1.parse(getTanggal);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                //change again to right format
                String day, monthString, year;
                day = (String) DateFormat.format("dd", date); // 20
                monthString = (String) DateFormat.format("MMM", date); // Jun
                year = (String) DateFormat.format("yyyy", date); // 2013


                if(tanggal!=null && bulan!=null && tahun!= null){
                    tanggal.setText(day);
                    bulan.setText(monthString);
                    tahun.setText(year);
                }
            }


            TextView servisTextView = (TextView) listItemView.findViewById(R.id.servis);
            if(servisTextView !=null ){
            servisTextView.setText(currentContent.getmServis());}

            TextView namaTextView = (TextView) listItemView.findViewById(R.id.nama_produk);
            if(namaTextView != null){
                namaTextView.setText(currentContent.getmMobil());}
            TextView statusTextView = (TextView) listItemView.findViewById(R.id.status);
            if(statusTextView != null){
                statusTextView.setText(currentContent.getmStatus());
            }
            TextView uidTextView = (TextView) listItemView.findViewById(R.id.text_uid);
            if(uidTextView !=null){
            uidTextView.setText(currentContent.getmUid());}

            ImageView statusceklis = (ImageView)listItemView.findViewById(R.id.status_ceklis);
            if(statusceklis != null){
                String status = currentContent.getMtanggalBook();
                if(status.equals(String.valueOf(0))) {
                    statusceklis.setVisibility(View.INVISIBLE);
                } else {
                    statusceklis.setVisibility(View.VISIBLE);
                }
            }

        return listItemView;

    }


}