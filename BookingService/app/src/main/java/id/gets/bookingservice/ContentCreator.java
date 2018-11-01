package id.gets.bookingservice;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContentCreator {

    /**
     * An array of items.
     */


    public static final List<String> hasilITEMS2 = new ArrayList<String>();

    public static final List<String> hasilAuth = new ArrayList<String>();
    public static final ArrayList<ListContent> history = new ArrayList<ListContent>();
    public static final ArrayList<ListContent> checklist = new ArrayList<ListContent>();
    public static final ArrayList<ListContent> sparepart = new ArrayList<ListContent>();
    public static final ArrayList<String> gambarPromo = new ArrayList<String>();
    public static final ArrayList<String> namaPromo = new ArrayList<String>();

    public static final String[] dataLogin = new String[7];
    /**
     * A map of data
     */

    public static final Map<String, RealItem> hasilITEM_MAP = new HashMap<String, RealItem>();

    public static final List<RealItem> servisData = new ArrayList<RealItem>();
    public static final List<RealItem> sparepartData = new ArrayList<RealItem>();


    public static class ParseJson {

        public ParseJson(String results) {
            if (results != null && !results.equals("")) {
                try {
                    JSONObject jsonObj = new JSONObject(results);
                    // Getting JSON Array node
                    JSONArray semuaHasil = jsonObj.getJSONArray("hasil");

                    String tanggal, servis, mobil, uid, status, iduser, hargaServis, totalBiaya;
                    history.clear();
                    // looping through All Hasil
                    for (int i = 0; i < semuaHasil.length(); i++) {
                        JSONObject c = semuaHasil.getJSONObject(i);
                        tanggal = c.getString("tgl_booking");
                        servis = c.getString("nama_servis");
                        mobil = c.getString("nama_mobil");
                        uid = c.getString("uniqueid_book");
                        status = c.getString("status");
                        iduser = c.getString("id_user");
                        hargaServis = c.getString("harga_servis");

                        history.add(new ListContent(tanggal, servis, mobil, uid, status, iduser, hargaServis));
                        // hasilITEM_MAP.put(realItem.no, realItem);
                    }

                } catch (final JSONException e) {
                    Log.e("ERROR HISTORY!!", "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e("ERROR HISTORY!!", "Couldn't get json from server.");
            }
        }
    }

    public static class ParseJsonAuth {

        public ParseJsonAuth(String results) {
            if (results != null && !results.equals("")) {
                try {
                    JSONObject jsonObj = new JSONObject(results);
                    // Getting JSON Array node
                    JSONArray semuaHasil = jsonObj.getJSONArray("hasil");
                    String Id, namaLengkap, noHp, email, alamat, message, username;

                    String status = jsonObj.getString("status");

                    // looping through All Hasil
                    if (status.equals("Ok")) {
                        for (int i = 0; i < semuaHasil.length(); i++) {
                            JSONObject c = semuaHasil.getJSONObject(i);
                            Id = c.getString("id");
                            namaLengkap = c.getString("nama_lengkap");
                            noHp = c.getString("noTlp");
                            email = c.getString("email");
                            alamat = c.getString("alamat");
                            username = c.getString("username");

                            dataLogin[0] = Id;
                            dataLogin[1] = namaLengkap;
                            dataLogin[2] = noHp;
                            dataLogin[3] = email;
                            dataLogin[4] = alamat;
                            dataLogin[5] = status;
                            dataLogin[6] = username;
                        }
                    } else {
                        for (int i = 0; i < semuaHasil.length(); i++) {
                            JSONObject c = semuaHasil.getJSONObject(i);
                            message = c.getString("message");
                            dataLogin[0] = message;
                            dataLogin[1]=""; dataLogin[2]="";dataLogin[3]="";dataLogin[4]="";
                            dataLogin[5] = status; dataLogin[6]="";
                        }
                    }

                } catch (final JSONException e) {
                    Log.e("ERROR AUTH!!", "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e("ERROR AUTH!!", "Couldn't get json from server.");
            }
        }
    }

    public static class ParseJsonPromo {

        public ParseJsonPromo(String results) {
            if (results != null && !results.equals("")) {
                try {
                    JSONObject jsonObj = new JSONObject(results);
                    String status = jsonObj.getString("status");
                    // Getting JSON Array node
                    gambarPromo.clear();
                    namaPromo.clear();
                    if (status.equals("ok")) {
                        String namaBanner, namaFile;
                        JSONArray jsonHasil = jsonObj.getJSONArray("hasil");
                        for (int i = 0; i < jsonHasil.length(); i++) {
                            JSONObject c = jsonHasil.getJSONObject(i);
                            namaBanner = c.getString("judul_promo");
                            namaFile = c.getString("filename");

                            gambarPromo.add(namaFile);
                            namaPromo.add(namaBanner);
                        }
                    } else {
                        gambarPromo.clear();
                        namaPromo.clear();
                        Log.e("ErrorString", "error");
                    }

                } catch (final JSONException e) {
                    Log.e("ERROR PROMO!!", "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e("ERROR PROMO!!", "Couldn't get json from server.");
            }
        }
    }

    public static class ParseJsonRep {

        public ParseJsonRep(String results) {
            if (results != null && !results.equals("")) {
                try {
                    JSONObject jsonObj = new JSONObject(results);
                    String status = jsonObj.getString("status");
                    // Getting JSON Array node
                    if (status.equals("ok")) {

                        String namaCheck, status_cb, namaSp, jumlahSp, hargaSp;
                        checklist.clear();
                        sparepart.clear();

                        JSONArray jsonChecklist = jsonObj.getJSONArray("checklist");
                            for (int i = 0; i < jsonChecklist.length(); i++) {
                                JSONObject c = jsonChecklist.getJSONObject(i);
                                namaCheck = c.getString("nama_checklist");
                                status_cb = c.getString("status_cb");
                                checklist.add(new ListContent(status_cb, namaCheck, "", "", "", "", ""));
                            }

                        JSONArray jsonSparepart = jsonObj.getJSONArray("sparepart");
                        for (int i = 0; i < jsonSparepart.length(); i++) {
                            JSONObject d = jsonSparepart.getJSONObject(i);
                            namaSp = d.getString("nama_sparepart");
                            jumlahSp = d.getString("jumlah");
                            hargaSp = d.getString("harga_spb");
                            sparepart.add(new ListContent(namaSp, jumlahSp, hargaSp, "", "", "", ""));
                        }
                    } else {
                        sparepart.clear();
                        checklist.clear();
                        Log.e("ErrorCBSPB", "error");
                    }

                } catch (final JSONException e) {
                    Log.e("ERROR REPORT!!", "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e("ERROR REPORT!!", "Couldn't get json from server.");
            }
        }
    }

    public static class ParseJsonPlh {

        public ParseJsonPlh(String results) {
            if (results != null && !results.equals("")) {
                try {
                    JSONObject jsonObj = new JSONObject(results);
                    // Getting JSON Array node
                    JSONArray servis = jsonObj.getJSONArray("servis");
                    JSONArray mobil = jsonObj.getJSONArray("mobil");
                    JSONArray sparepart = jsonObj.getJSONArray("sparepart");

                    sparepartData.clear();
                    servisData.clear();
                    hasilITEMS2.clear();
                    String idServis, namaServis, hargaServis, namaMobilServ,
                            namaMobil,
                            idSparepart, namaSparepart, hargaSparepart, namaMobilSpare;

                    // looping through All Hasil

                    for (int i = 0; i < servis.length(); i++) {
                        JSONObject c = servis.getJSONObject(i);
                        idServis = c.getString("id_servis");
                        namaServis = c.getString("nama_servis");
                        hargaServis = c.getString("harga_servis");
                        namaMobilServ = c.getString("mobil");
                        RealItem realItem = new RealItem(idServis, namaServis, hargaServis, namaMobilServ);
                        servisData.add(realItem);
                    }

                    // looping through All Hasil
                    for (int i = 0; i < mobil.length(); i++) {
                        JSONObject c = mobil.getJSONObject(i);
                        namaMobil = c.getString("nama_mobil");
                        hasilITEMS2.add(namaMobil);
                    }

                    // looping through All Hasil
                    for (int i = 0; i < sparepart.length(); i++) {
                        JSONObject c = sparepart.getJSONObject(i);
                        idSparepart = c.getString("id_sparepart");
                        namaSparepart = c.getString("nama_sparepart");
                        hargaSparepart = c.getString("harga_sparepart");
                        namaMobilSpare = c.getString("mobil");
                        RealItem realItem = new RealItem(idSparepart, namaSparepart, hargaSparepart, namaMobilSpare);
                        sparepartData.add(realItem);
                    }

                } catch (final JSONException e) {
                    Log.e("ERROR PILIH!!", "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e("ERROR PILIH!!", "Couldn't get json from server.");
            }
        }
    }

    //untuk custom list
    public static class RealItem {
        String no, nama, harga, mobil;

        public RealItem(String id, String name, String price, String car) {
            no = id;
            nama = name;
            harga = price;
            mobil = car;
        }
    }

}





