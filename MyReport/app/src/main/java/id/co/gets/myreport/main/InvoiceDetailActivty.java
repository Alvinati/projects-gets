package id.co.gets.myreport.main;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.co.gets.myreport.R;
import id.co.gets.myreport.dbhelper.DbHelper;
import id.co.gets.myreport.network.JSONParser;

public class InvoiceDetailActivty extends AppCompatActivity {


    DbHelper mDbHelper;
    String [] dataUser, dataMaybank;
    String myId, namaVendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        String iId = getIntent().getStringExtra("ID");

        JSONParser.InvoiceItem invoiceData = JSONParser.invoiceItem_MAP.get(iId);

        String date = invoiceData.tglInvoice;
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            Log.e("error parsing date", e.getMessage());
        }
        spf = new SimpleDateFormat("EEEE, dd MMM yyyy");
        date = spf.format(newDate);

        mDbHelper = new DbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM entry", null);
        cursor.moveToFirst();
        myId = "";
        String rawUser = "", rawMaybank = "";
        if (cursor.moveToFirst()) {
            do {
                myId = cursor.getString(0);
                namaVendor = cursor.getString(cursor.getColumnIndex("vendor"));
                rawUser = cursor.getString(cursor.getColumnIndex("user"));
                rawMaybank = cursor.getString(cursor.getColumnIndex("maybank"));
            } while (cursor.moveToNext());
        }
        cursor.close();


        dataUser = rawUser.split("&&");
        dataMaybank = rawMaybank.split("&&");


        ((TextView) findViewById(R.id.det_in_name)).setText(dataUser[4]);
        ((TextView) findViewById(R.id.det_in_kota)).setText(dataUser[8]);
        ((TextView) findViewById(R.id.det_in_co)).setText(dataMaybank[0]);
        ((TextView) findViewById(R.id.det_in_tujuan)).setText(dataMaybank[1]);
        ((TextView) findViewById(R.id.det_in_alamat)).setText(dataMaybank[3]);

        ((TextView) findViewById(R.id.det_in_tanggal)).setText(date);
        ((TextView) findViewById(R.id.invoice_number)).setText(invoiceData.nomorInvoice);
        ((TextView)findViewById(R.id.txt_nom_tko)).setText(invoiceData.nomTko);
        ((TextView)findViewById(R.id.txt_jml_tko)).setText(invoiceData.jmlTko);
        ((TextView)findViewById(R.id.txt_nom_bpjs)).setText(invoiceData.nomBpjs);
        ((TextView)findViewById(R.id.txt_jml_bpjs)).setText(invoiceData.jmlBpjs);
        ((TextView)findViewById(R.id.txt_nom_temporary)).setText(invoiceData.nomTemporary);
        ((TextView)findViewById(R.id.txt_jml_temporary)).setText(invoiceData.jmlTemporary);
        ((TextView)findViewById(R.id.txt_nom_patroli)).setText(invoiceData.nomPatroli);
        ((TextView)findViewById(R.id.txt_nom_lembur)).setText(invoiceData.nomLembur);
        ((TextView)findViewById(R.id.txt_nom_koneksi)).setText(invoiceData.nomKoneksi);
        ((TextView)findViewById(R.id.txt_jml_koneksi)).setText(invoiceData.jmlKoneksi);


    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }
}
