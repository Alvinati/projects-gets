package id.gets.bookingservice;



import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
    public class NetworksUtil {

    public final static String BASE_URL = "http://choirunisata.000webhostapp.com/servicebook/honda/";

    final static String BS_AUTH_URL = BASE_URL + "api/authapi/tb_users?";

    final static String BS_HIS_URL = BASE_URL + "api/historyapi?";

    final static String BS_REP_URL = BASE_URL + "api/reportapi?";
    final static String BS_PLH_URL = BASE_URL + "api/pilihapi";
    final static String BS_PROMO_URL = BASE_URL + "search/tb_promo";


    final static String PARAM_JUMLAH = "banyakWisata";


    /*
     * The sort field. One of stars, forks, or updated.
     * Default: results are sorted by best match if no field is specified.
     */
    final static String PARAM_OFF = "offsetData";
    final static String offsetData = "0";
    final static String PARAM_EMAIL = "email";
    final static String PARAM_PASS = "password";
    final static String PARAM_ID = "id";
    final static String PARAM_UID_BOOK = "uniqueid_book";
    //final static String PARAM_TEMPAT = "kabupatenKotaWisata";
    /**
     * Builds the URL used to query Github.
     *
     //* @param noneed, longitude The keyword that will be queried for.
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrlPromo() {

        Uri builtUri=
                Uri.parse(BS_PROMO_URL).buildUpon()
                        .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());

        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }


    public static URL buildUrlPlh() {

        Uri builtUri=
                Uri.parse(BS_PLH_URL).buildUpon()
                        .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());

        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlAuth(String email, String password) {
        Log.e("Login", email);
        Uri builtUri=
                Uri.parse(BS_AUTH_URL).buildUpon()
                        .appendQueryParameter(PARAM_EMAIL, email)
                        .appendQueryParameter(PARAM_PASS, password)
                        .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());

        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlHis(String id) {
        Uri builtUri=
                Uri.parse(BS_HIS_URL).buildUpon()
                        .appendQueryParameter(PARAM_ID, id)
                        .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());

        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlRep(String uid) {
        Uri builtUri=
                Uri.parse(BS_REP_URL).buildUpon()
                        .appendQueryParameter(PARAM_UID_BOOK, uid)
                        .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());

        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


}