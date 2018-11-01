package id.co.gets.myreport.network;

import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONParser {

    public static final String[] dataLogin = new String[10];
    public static final String[] dataMaybank = new String[6];
    public static final List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
    public static final Map<String, InvoiceItem> invoiceItem_MAP = new HashMap<String, InvoiceItem>();
    public static  String invoiceKosong;

    public static class ParseJson {

        public ParseJson(String results) {
            if(results != null && !results.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    String status = jsonObject.getString("status");

                    JSONArray jsonArray = jsonObject.getJSONArray("hasil");

                    if(status.equals("Ok")) {
                        for(int i = 0; i<jsonArray.length(); i++) {
                            JSONObject c = jsonArray.getJSONObject(i);
                            dataLogin[0] = status;
                            dataLogin[1] = c.getString("id_users");
                            dataLogin[2] = c.getString("username");
                            dataLogin[3] = c.getString("nama_vendor");
                            dataLogin[4] = c.getString("nama_lengkap");
                            dataLogin[5]= c.getString("email");
                            dataLogin[6] = c.getString("no_telp");
                            dataLogin[7] = c.getString("alamat");
                            dataLogin[8] = c.getString("kota");
                            dataLogin[9] = c.getString("grup");
                        }
                    } else {
                        for (int k=0; k<jsonArray.length(); k++) {
                            JSONObject e = jsonArray.getJSONObject(k);
                            dataLogin[0] = status;
                            dataLogin[1] = e.getString("message");
                            dataLogin[2] = ""; dataLogin[3] = "";
                            dataLogin[4] = ""; dataLogin[5] = "";
                            dataLogin[6] = ""; dataLogin[7] = "";
                            dataLogin[8] = ""; dataLogin[9] = "";
                        }
                    }

                } catch (final JSONException e) {
                    Log.e("ERROR LOGIN!!", "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e("ERROR LOGIN!!", "Couldn't get json from server");
            }
        }

        public ParseJson(String results, int maybankUser) {
            if(results != null && !results.equals("")){
                try {
                    JSONObject hasil = new JSONObject(results);
                    String status = hasil.getString("status");
                    JSONArray hasilData= hasil.getJSONArray("hasil");

                    if(status.equals("ok")) {
                        for (int k = 0; k < hasilData.length(); k++) {
                            JSONObject e = hasilData.getJSONObject(k);
                            dataMaybank[0] = e.getString("company");
                            dataMaybank[1] = e.getString("name");
                            dataMaybank[2] = e.getString("telp_co");
                            dataMaybank[3] = e.getString("address");
                            dataMaybank[4] = e.getString("token");
                            dataMaybank[5] = status;
                        }
                    } else {
                       dataMaybank[0] = ""; dataMaybank[1]=""; dataMaybank[2]="";
                       dataMaybank[3] = ""; dataMaybank[4]=""; dataMaybank[5]="NG";
                    }
                } catch (final JSONException e) {
                    Log.e("ERROR DATA INVOICE!", "Json parsing error:" +e.getMessage());
                }
            } else {
                Log.e("ERROR DATA INVOICE!", "Couldn't get json from server");
            }
        }

        public ParseJson(String results, String invoice){
            if(results != null && !results.equals("")){
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    String status = jsonObject.getString("status");
                    JSONArray dataInvoice = jsonObject.getJSONArray("hasil");

                    invoiceItems.clear();

                    String  idInvoice, nomorInvoice, tglInvoice, nomTko, jmlTko, nomBpjs, jmlBpjs,
                            nomLembur, jmlLembur, nomKoneksi, jmlKoneksi,
                            nomPatroli, nomTemporary, jmlTemporary;

                    if(status.equals("Ok")){
                        for(int i = 0; i<dataInvoice.length(); i++){
                            JSONObject d = dataInvoice.getJSONObject(i);
                            idInvoice = d.getString("id_invoice");
                            nomorInvoice = d.getString("no_invoice");
                            tglInvoice = d.getString("tgl_input");
                            nomTko = d.getString("nom_tko");
                            jmlTko = d.getString("jml_tko");
                            nomBpjs = d.getString("nom_bpjs");
                            jmlBpjs = d.getString("jml_bpjs");
                            nomLembur = d.getString("nom_lembur");
                            nomKoneksi = d.getString("nom_koneksi");
                            jmlKoneksi = d.getString("jml_koneksi");
                            nomPatroli = d.getString("patroli");
                            nomTemporary = d.getString("nom_temporary");
                            jmlTemporary = d.getString("jml_temporary");

                            InvoiceItem invoiceItem = new InvoiceItem(idInvoice,
                                    nomorInvoice, tglInvoice, nomTko, jmlTko, nomBpjs, jmlBpjs,
                                    nomLembur, nomKoneksi, jmlKoneksi, nomPatroli, nomTemporary,
                                    jmlTemporary);

                            invoiceItems.add(invoiceItem);
                            invoiceItem_MAP.put(idInvoice, invoiceItem);
                        }
                    } else if (status.equals("NG")){
                        invoiceItems.clear();
                        invoiceItem_MAP.clear();
                        invoiceKosong = dataInvoice.getJSONObject(0).getString("message");
                    }

                } catch(final JSONException e ){
                    Log.e("ERROR DATA INVOICE!", "Json parsing error:" +e.getMessage());
                }
            } else {
                Log.e("ERROR DATA INVOICE!", "Couldn't get json from server");
            }
        }

    }

    public static class InvoiceItem {

        public final String  idInvoice, nomorInvoice, tglInvoice, nomTko, jmlTko, nomBpjs, jmlBpjs,
                nomLembur, nomKoneksi, jmlKoneksi,
                nomPatroli, nomTemporary, jmlTemporary;

        public InvoiceItem(String id, String no, String tgl, String tko1, String tko2, String bpjs1,
                        String bpjs2, String lembur1, String koneksi1, String koneksi2,
                        String patroli, String temporary1,String temporary2) {

            this.idInvoice = id;
            this.tglInvoice = tgl;
            this.nomorInvoice = no;
            this.nomTko = tko1; this.jmlTko = tko2;
            this.nomBpjs = bpjs1; this.jmlBpjs = bpjs2;
            this.nomLembur = lembur1;
            this.nomKoneksi = koneksi1; this.jmlKoneksi = koneksi2;
            this.nomPatroli = patroli;
            this.nomTemporary = temporary1; this.jmlTemporary = temporary2;
        }
    }

}




