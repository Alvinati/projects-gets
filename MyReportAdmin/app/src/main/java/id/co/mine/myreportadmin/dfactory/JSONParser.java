package id.co.mine.myreportadmin.dfactory;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONParser {

    public static final String[] dataLogin = new String[9];
    public static final List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
    public static final Map<String, InvoiceItem> invoiceItem_MAP = new HashMap<String, InvoiceItem>();
    public static final List<ApelItem> apelItems = new ArrayList<ApelItem>();
    public static final List<TkoItem> tkoItems = new ArrayList<TkoItem>();
    public static final List<NotifItem> notifItems = new ArrayList<NotifItem>();
    public static final List<UserItem> userItems = new ArrayList<UserItem>();
    public static final List<TokenItem> tokenItems = new ArrayList<TokenItem>();

    public static  String invoiceKosong, apelKosong, notifKosong, userKosong;

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
                            dataLogin[1] = c.getString("id_muser");
                            dataLogin[2] = c.getString("username");
                            dataLogin[3] = c.getString("company");
                            dataLogin[4] = c.getString("name");
                            dataLogin[5] = c.getString("email");
                            dataLogin[6] = c.getString("handphone");
                            dataLogin[7] = c.getString("address");
                            dataLogin[8] = c.getString("kota");
                        }

                    } else {
                        for (int k=0; k<jsonArray.length(); k++) {
                            JSONObject e = jsonArray.getJSONObject(k);
                            dataLogin[0] = status;
                            dataLogin[1] = e.getString("message");
                            dataLogin[2] = ""; dataLogin[3] = "";
                            dataLogin[4] = ""; dataLogin[5] = "";
                            dataLogin[6] = ""; dataLogin[7] = "";
                            dataLogin[8] = "";
                        }
                    }

                } catch (final JSONException e) {
                    Log.e("ERROR LOGIN!!", "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e("ERROR LOGIN!!", "Couldn't get json from server");
            }
        }

        //string param ke 2 hanya untuk membedakan -- parse untuk data invoice
        public ParseJson(String results, String invoice){
            if(results != null && !results.equals("")){
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    String status = jsonObject.getString("status");
                    JSONArray dataInvoice = jsonObject.getJSONArray("hasil");

                    invoiceItems.clear();
                    invoiceItem_MAP.clear();

                    String  idInvoice, nomorInvoice, tglInvoice, nomTko, jmlTko, nomBpjs, jmlBpjs,
                            nomLembur, jmlLembur, nomKoneksi, jmlKoneksi,
                            nomPatroli, nomTemporary, jmlTemporary, namaVendor;

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
                            namaVendor = d.getString("nama_vendor");

                            InvoiceItem invoiceItem = new InvoiceItem(idInvoice,
                                    nomorInvoice, tglInvoice, nomTko, jmlTko, nomBpjs, jmlBpjs,
                                    nomLembur, nomKoneksi, jmlKoneksi, nomPatroli, nomTemporary,
                                    jmlTemporary, namaVendor);

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

        //int param ke 2 hanya untuk membedakan -- parse untuk data apel jaring
        public ParseJson(String results, int apel) {
            if(results != null && !results.equals("")){
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    String status = jsonObject.getString("status");
                    JSONArray dataInvoice = jsonObject.getJSONArray("hasil");

                    apelItems.clear();
                    String  idApel, nama, tglInput, jamInput, statusApel;

                    if(status.equals("Ok")){
                        for(int i = 0; i<dataInvoice.length(); i++){
                            JSONObject d = dataInvoice.getJSONObject(i);
                            idApel = d.getString("id_apel");
                            nama = d.getString("nama_vendor");
                            tglInput = d.getString("tgl_input");
                            jamInput = d.getString("jam_input");
                            statusApel = d.getString("status_apel");


                            ApelItem apelItem = new ApelItem(idApel,
                                    nama, tglInput, jamInput, statusApel);

                            apelItems.add(apelItem);
                        }
                    } else if (status.equals("NG")){
                        apelItems.clear();
                        apelKosong = dataInvoice.getJSONObject(0).getString("message");
                    }

                } catch(final JSONException e ){
                    Log.e("ERROR DATA APEL!", "Json parsing error:" +e.getMessage());
                }
            } else {
                Log.e("ERROR DATA APEL!", "Couldn't get json from server");
            }
        }

        //int dan string param ke 2 dan 3 hanya untuk membedakan -- parse untuk data tko
        public ParseJson(String results, int tko, String just) {
            if(results != null && !results.equals("")){
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    String status = jsonObject.getString("status");
                    JSONArray dataInvoice = jsonObject.getJSONArray("hasil");

                    tkoItems.clear();
                    String  idTko, nama, tglInput, jamInput, shift, shiftt1, week ;

                    if(status.equals("Ok")){
                        for(int i = 0; i<dataInvoice.length(); i++){
                            JSONObject d = dataInvoice.getJSONObject(i);
                            idTko = d.getString("id_tko");
                            nama = d.getString("nama_vendor");
                            tglInput = d.getString("tgl_input");
                            jamInput = d.getString("jam_input");
                            shift = d.getString("shift");
                            shiftt1  = d.getString("nonshift");
                            week = d.getString("week");

                            TkoItem tkoItem = new TkoItem(idTko,
                                    nama, tglInput, jamInput, shift, shiftt1, week);
                            tkoItems.add(tkoItem);
                        }
                    }

                } catch(final JSONException e ){
                    Log.e("ERROR DATA TKO!", "Json parsing error:" +e.getMessage());
                }
            } else {
                Log.e("ERROR DATA TKO!", "Couldn't get json from server");
            }
        }

        public ParseJson(String results, long tokenRegis) {
            if(results != null && !results.equals("")){
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    String status = jsonObject.getString("status");
                    JSONArray dataToken = jsonObject.getJSONArray("hasil");

                    tokenItems.clear();
                    String  idToken, token, username, tglUpdate;

                    if(status.equals("ok")){
                        for(int i = 0; i<dataToken.length(); i++){
                            JSONObject d = dataToken.getJSONObject(i);
                            idToken = d.getString("id_token");
                            token = d.getString("token");
                            username = d.getString("user_update");
                            tglUpdate = d.getString("tgl_update");

                            TokenItem tokenItem = new TokenItem(idToken, token, username, tglUpdate);
                            tokenItems.add(tokenItem);
                        }
                    } else if (status.equals("NG")){
                        tokenItems.clear();
                    }

                } catch(final JSONException e ){
                    Log.e("ERROR DATA TOKEN!", "Json parsing error:" +e.getMessage());
                }
            } else {
                Log.e("ERROR DATA TOKEN!", "Couldn't get json from server");
            }
        }


    }

    public static class ParseJsonNotif {
        public ParseJsonNotif(String results) {
            if(results != null && !results.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    String status = jsonObject.getString("status");

                    JSONArray jsonArray = jsonObject.getJSONArray("hasil");
                    notifItems.clear();
                    String idnotif, namavendor, tipe, pesan, tgl, jam, readState;
                    if(status.equals("ok")) {
                        for(int i = 0; i<jsonArray.length(); i++) {
                            JSONObject c = jsonArray.getJSONObject(i);
                            idnotif = c.getString("id_notif");
                            namavendor = c.getString("nama_vendor");
                            tipe = c.getString("tipe_pesan");
                            pesan = c.getString("pesan");
                            tgl = c.getString("tgl_input");
                            jam = c.getString("jam_input");
                            readState = c.getString("status");
                            notifItems.add(new NotifItem(idnotif, namavendor, tgl, jam, tipe, pesan, readState));
                        }

                    } else {
                        notifItems.clear();
                        notifKosong = "belum ada notifikasi";
                    }

                } catch (final JSONException e) {
                    Log.e("ERROR LOGIN!!", "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e("ERROR LOGIN!!", "Couldn't get json from server");
            }
        }
    }


    public static class ParseJsonUsers {
        public ParseJsonUsers(String results) {
            if(results != null && !results.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    String status = jsonObject.getString("status");

                    JSONArray jsonArray = jsonObject.getJSONArray("hasil");
                    userItems.clear();
                    String idusers, grup, namavendor, namaLengkap, email, tgl, kota, alamat;
                    if(status.equals("ok")) {
                        for(int i = 0; i<jsonArray.length(); i++) {
                            JSONObject c = jsonArray.getJSONObject(i);
                            idusers = c.getString("id_users");
                            grup = c.getString("grup");
                            namavendor = c.getString("nama_vendor");
                            namaLengkap = c.getString("nama_lengkap");
                            email = c.getString("email");
                            tgl = c.getString("tgl_input");
                            kota = c.getString("kota");
                            alamat = c.getString("alamat");
                            userItems.add(new UserItem(idusers, grup, namavendor, tgl, namaLengkap, email, kota, alamat));
                        }

                    } else {
                        userItems.clear();
                        userKosong = "Belum ada user";
                    }

                } catch (final JSONException e) {
                    Log.e("ERROR LOGIN!!", "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e("ERROR LOGIN!!", "Couldn't get json from server");
            }
        }
    }

    public static class InvoiceItem {

        public final String  idInvoice, nomorInvoice, tglInvoice, nomTko, jmlTko, nomBpjs, jmlBpjs,
                nomLembur, nomKoneksi, jmlKoneksi,
                nomPatroli, nomTemporary, jmlTemporary, vendorName;

        public InvoiceItem(String id, String no, String tgl, String tko1, String tko2, String bpjs1,
                           String bpjs2, String lembur1, String koneksi1, String koneksi2,
                           String patroli, String temporary1,String temporary2, String vendor) {

            this.idInvoice = id;
            this.tglInvoice = tgl;
            this.nomorInvoice = no;
            this.nomTko = tko1; this.jmlTko = tko2;
            this.nomBpjs = bpjs1; this.jmlBpjs = bpjs2;
            this.nomLembur = lembur1;
            this.nomKoneksi = koneksi1; this.jmlKoneksi = koneksi2;
            this.nomPatroli = patroli;
            this.nomTemporary = temporary1; this.jmlTemporary = temporary2;
            this.vendorName = vendor;

        }
    }

    public static class ApelItem {

        public final String  idApel, namaVendor, tglRecord, jamRecord, status;

        public ApelItem(String id, String nama, String tgl, String jam, String status) {
            this.idApel = id;
            this.namaVendor = nama;
            this.tglRecord = tgl;
            this.jamRecord = jam;
            this.status = status;
        }
    }

    public static class TkoItem {

        public final String  idTko, namaVendor, tglRecord, jamRecord, shift, nonshift, week;

        public TkoItem(String id, String nama, String tgl, String jam, String shift, String nonshift, String week) {
            this.idTko = id;
            this.namaVendor = nama;
            this.tglRecord = tgl;
            this.jamRecord = jam;
            this.shift = shift;
            this.nonshift = nonshift;
            this.week = week;
        }
    }


    public static class NotifItem {

        public final String  idNotif, namaVendor, tipePesan, pesan, tglInput, jamInput, status;

        public NotifItem(String id, String nama, String tgl, String jam, String type,
                         String message, String status) {
            this.idNotif = id;
            this.namaVendor = nama;
            this.tglInput = tgl;
            this.jamInput = jam;
            this.tipePesan = type;
            this.pesan = message;
            this.status = status;
        }
    }

    public static class UserItem {

        public final String  idUser, grup, namaVendor, namaLengkap, email, tglInput, kota, alamat;
        public UserItem(String id, String grup, String nama, String tgl, String fullname,
                         String email, String kota, String alamat) {
            this.idUser = id;
            this.grup = grup;
            this.namaVendor = nama;
            this.tglInput = tgl;
            this.namaLengkap = fullname;
            this.email = email;
            this.kota = kota;
            this.alamat = alamat;
        }
    }

    public static class TokenItem {
        public final String idToken, token, username, tglUpdate;

        public TokenItem(String id, String token, String username, String tgl) {
            this.idToken = id;
            this.token = token;
            this.username = username;
            this.tglUpdate = tgl;
        }
    }

}




