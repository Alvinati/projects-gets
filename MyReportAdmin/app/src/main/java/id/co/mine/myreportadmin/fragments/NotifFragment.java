package id.co.mine.myreportadmin.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.mine.myreportadmin.BuildConfig;
import id.co.mine.myreportadmin.R;
import id.co.mine.myreportadmin.dfactory.JSONParser;
import id.co.mine.myreportadmin.dfactory.SingletonRequestQueue;

public class NotifFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ProgressBar pbNotif;
    View rootView;
    TextView textView;
    SwipeRefreshLayout swipeRefreshLayout;
    public NotifFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (rootView !=null) {
            pbNotif.setVisibility(View.VISIBLE);
            notifGET(rootView);

             swipeRefreshLayout = rootView.findViewById(R.id.notif_swipe);
             swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    notifGET(rootView);

                }
            });
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_notif, container, false);

        mRecyclerView = rootView.findViewById(R.id.notif_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        textView = rootView.findViewById(R.id.notif_kosong);
        textView.setVisibility(View.INVISIBLE);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        registerForContextMenu(mRecyclerView);

        pbNotif = rootView.findViewById(R.id.pb_notif);
        pbNotif.bringToFront();
        pbNotif.setVisibility(View.INVISIBLE);

        return rootView;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        JSONParser.NotifItem items = ((NotificationAdapter)mRecyclerView.getAdapter()).getPosition();

        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteNotifPOST(items.idNotif);
                break;
            default:
                break;
        }
        return true;
    }

    public void notifGET(final View view) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/base/tb_notif";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbNotif.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);

                new JSONParser.ParseJsonNotif(response);
                List<JSONParser.NotifItem> myDataset = JSONParser.notifItems;

                Log.e("mydataset", myDataset.toString());
                if(myDataset.toString().equals("[]")){
                    textView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    textView.setText(JSONParser.notifKosong);
                } else if(!myDataset.toString().equals("[]")) {
                    textView.setVisibility(View.GONE);
                    Fragment user = new UsersFragment();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mAdapter = new NotificationAdapter(myDataset, user);
                    mRecyclerView.setAdapter(mAdapter);
                }

            }
        }, errorListener){
            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };
        queue.add(stringRequest);
    }

    private void deleteNotifPOST(final String id) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/smallupdate";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                pbNotif.setVisibility(View.VISIBLE);
                notifGET(rootView);
            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }

            @Override
            public Map getParams() {
                Map params = new HashMap();

                params.put("id", id);
                params.put("delete", "notif");

                return params;
            }

            @Override
            public Map getHeaders() throws
                    AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-TYpe", "application/x-www-form-urlencoded; charset=utf-8");
                return headers;
            }
        };

        queue.add(stringRequest);
    }

    Response.ErrorListener errorListener = new
            Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof NetworkError) {
                        pbNotif.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "No network available", Toast.LENGTH_LONG).show();
                    } else {
                        pbNotif.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };
}
