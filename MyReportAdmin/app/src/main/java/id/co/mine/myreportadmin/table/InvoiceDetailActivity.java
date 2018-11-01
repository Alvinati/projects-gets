package id.co.mine.myreportadmin.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.co.mine.myreportadmin.R;
import id.co.mine.myreportadmin.dfactory.JSONParser;

public class InvoiceDetailActivity extends AppCompatActivity {


    String [] dataUser, dataMaybank;
    String myId, namaVendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle("Detail Invoice");

        String iId = getIntent().getStringExtra("id");

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

        ((TextView) findViewById(R.id.txt_nama_vendor)).setText(invoiceData.vendorName);

        ((TextView) findViewById(R.id.txt_in_tgl)).setText(date);
        ((TextView) findViewById(R.id.txt_no_invoice)).setText(invoiceData.nomorInvoice);
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

}
