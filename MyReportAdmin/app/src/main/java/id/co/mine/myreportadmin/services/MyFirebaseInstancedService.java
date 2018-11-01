package id.co.mine.myreportadmin.services;


import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import id.co.mine.myreportadmin.BuildConfig;
import id.co.mine.myreportadmin.dfactory.SingletonRequestQueue;


public class MyFirebaseInstancedService extends FirebaseInstanceIdService {
   private static final String TAG = "MyFirebaseIdService";
   private static final String TOPIC_GLOBAL = "global";

    public static String thisToken;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);

        sendRegistrationToServer(refreshedToken);
    }



    private void sendRegistrationToServer(final String token) {

        thisToken = token;

    }


}
