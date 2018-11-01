package id.co.mine.myreportadmin.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
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
import id.co.mine.myreportadmin.main.MainActivity;


public class UsersFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ProgressBar pbUsers;
    View rootView;
    TextView textView;
    ProgressBar pbCreateUser;
    String group;
    SwipeRefreshLayout swipeRefreshLayout;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (rootView !=null) {
            pbUsers.setVisibility(View.VISIBLE);
            usersGET(rootView);

            swipeRefreshLayout = rootView.findViewById(R.id.users_swipe);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    usersGET(rootView);

                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =inflater.inflate(R.layout.fragment_users, container, false);

        mRecyclerView = rootView.findViewById(R.id.users_recyclerview);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        textView = rootView.findViewById(R.id.users_kosong);
        textView.setVisibility(View.GONE);
        registerForContextMenu(mRecyclerView);

        FloatingActionButton addUser = rootView.findViewById(R.id.users_fab);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser(getContext());
            }
        });

        pbUsers = rootView.findViewById(R.id.pb_users);
        pbUsers.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
       JSONParser.UserItem items = ((UsersAdapter)mRecyclerView.getAdapter()).getPosition();

        switch (item.getItemId()) {
            case R.id.action_reset:
                resetPassPOST(items.email);
                break;
            case R.id.action_lihat:
                lihatUser(getContext(), items);
                break;
            case R.id.action_delete:
               deleteUserPOST(items.idUser);
                break;
            default:
                break;
        }

        return true;
    }

    public void usersGET(final View view) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/base/tb_users";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbUsers.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);

                new JSONParser.ParseJsonUsers(response);
                List<JSONParser.UserItem> myDataset = JSONParser.userItems;

                if(myDataset.toString().equals("[]")){
                    mRecyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(JSONParser.userKosong);
                } else if(myDataset != null && !myDataset.toString().equals("[]")) {
                    textView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mAdapter = new UsersAdapter(myDataset);
                    mRecyclerView.setAdapter(mAdapter);
                }

            }
        }, errorListener){
            @Override
            public Priority getPriority() {
                return Priority.NORMAL;
            }
        };
        queue.add(stringRequest);

    }

    Response.ErrorListener errorListener = new
            Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof NetworkError) {
                        pbUsers.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "No network available", Toast.LENGTH_LONG).show();
                    } else {
                        pbUsers.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };


    private void createUser(final Context context) {
        final android.support.v7.app.AlertDialog createUserDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = ((MainActivity)context).getLayoutInflater();
        final View createUserDetail = inflater.inflate(R.layout.new_user, null);
        pbCreateUser = createUserDetail.findViewById(R.id.pb_cre);
        pbCreateUser.setVisibility(View.INVISIBLE);

        final TextView judul = (TextView)createUserDetail.findViewById(R.id.alert_pop_judul);
        judul.setText("Create new user");

        final TableRow trVendor = createUserDetail.findViewById(R.id.row_vendor);
        final TableRow trLkc = createUserDetail.findViewById(R.id.row_lkc);
        final TableRow trRegion = createUserDetail.findViewById(R.id.row_region);
        final TableRow trKota = createUserDetail.findViewById(R.id.row_kota);

        Spinner sp = (Spinner)createUserDetail.findViewById(R.id.cre_sp_usergroup);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context, R.array.group,
                android.R.layout.simple_spinner_dropdown_item
                );
        sp.setAdapter(arrayAdapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String namaGrup = adapterView.getItemAtPosition(i).toString();

                if(namaGrup.equals("Vendor")) {
                    group = "1";
                    trRegion.setVisibility(View.GONE);
                    trLkc.setVisibility(View.GONE);
                    trVendor.setVisibility(View.VISIBLE);
                    trKota.setVisibility(View.VISIBLE);
                } else if(namaGrup.equals("CMS")) {
                    group = "2";
                    trRegion.setVisibility(View.VISIBLE);
                    trLkc.setVisibility(View.VISIBLE);
                    trVendor.setVisibility(View.GONE);
                    trKota.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final AutoCompleteTextView acRegion = createUserDetail.findViewById(R.id.cre_ac_region);
        ArrayAdapter<CharSequence> arrayAdapter2 = ArrayAdapter.createFromResource(context, R.array.region
                , android.R.layout.simple_dropdown_item_1line);
        acRegion.setAdapter(arrayAdapter2);

        createUserDialog.setView(createUserDetail);
        createUserDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String getUsername = ((EditText)createUserDetail.findViewById(R.id.cre_et_username)).getText().toString();
                String getName = ((EditText)createUserDetail.findViewById(R.id.cre_et_nama)).getText().toString();
                String getEmail = ((EditText)createUserDetail.findViewById(R.id.cre_et_email)).getText().toString();

                if(group.equals("2")) {
                    EditText editText = (EditText)createUserDetail.findViewById(R.id.cre_et_KCI);
                    EditText editText2 = (EditText)createUserDetail.findViewById(R.id.cre_et_KCP);

                    String region = acRegion.getText().toString();
                    String kci = editText.getText().toString();
                    String kcp = editText2.getText().toString();

                    if(getUsername.equals("") || getName.equals("") || getEmail.equals("") || region.equals("")
                            || kci.equals("") || kcp.equals("")){
                        Toast.makeText(context, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                    } else {
                        pbCreateUser.setVisibility(View.VISIBLE);
                        registerPOST(group, getUsername, getName, getEmail, "123456", kcp, kci, region);
                    }

                } else if(group.equals("1")) {

                    EditText editText3 = (EditText)createUserDetail.findViewById(R.id.cre_et_vendor);
                    EditText editText4 = (EditText)createUserDetail.findViewById(R.id.cre_et_kota) ;

                    String getVendor = editText3.getText().toString();
                    String getKota = editText4.getText().toString();

                    if(getUsername.equals("") || getName.equals("") || getEmail.equals("") || getKota.equals("") ||
                            getVendor.equals("") ){
                        Toast.makeText(context, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                    } else {
                        pbCreateUser.setVisibility(View.VISIBLE);
                        registerPOST(group, getUsername, getName, getEmail, "123456", getVendor, "", getKota);
                    }
                }

                createUserDialog.dismiss();
            }
        });

        createUserDialog.show();
    }

    private void lihatUser(Context context, JSONParser.UserItem items) {
        final android.support.v7.app.AlertDialog editUserDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = ((MainActivity)context).getLayoutInflater();
        final View editUserDetail = inflater.inflate(R.layout.new_user, null);
        pbCreateUser = editUserDetail.findViewById(R.id.pb_cre);
        pbCreateUser.setVisibility(View.INVISIBLE);

        editUserDialog.setView(editUserDetail);

        final TableRow trGroup = (TableRow)editUserDetail.findViewById(R.id.row_group);
        trGroup.setVisibility(View.GONE);
        final TableRow trUsername = (TableRow)editUserDetail.findViewById(R.id.row_username);
        trUsername.setVisibility(View.GONE);
        final TableRow trRegion = (TableRow)editUserDetail.findViewById(R.id.row_region);
        final TableRow trLkc = (TableRow)editUserDetail.findViewById(R.id.row_lkc);
        final TableRow trVendor = (TableRow)editUserDetail.findViewById(R.id.row_vendor);
        final TableRow trKota = (TableRow)editUserDetail.findViewById(R.id.row_kota);

        final EditText etName = (EditText)editUserDetail.findViewById(R.id.cre_et_nama);
        final EditText etEmail = (EditText)editUserDetail.findViewById(R.id.cre_et_email);
        final TextView judul = (TextView)editUserDetail.findViewById(R.id.alert_pop_judul);

        if(items.grup.equals("1")) {
            trRegion.setVisibility(View.GONE);
            trLkc.setVisibility(View.GONE);
            EditText etVendor = (EditText)editUserDetail.findViewById(R.id.cre_et_vendor);
            EditText etKota = (EditText)editUserDetail.findViewById(R.id.cre_et_kota);

            etVendor.setText(items.namaVendor);
            etVendor.setInputType(InputType.TYPE_NULL);
            etKota.setText(items.kota);
            etKota.setInputType(InputType.TYPE_NULL);
        } else if(items.grup.equals("2")) {
            trKota.setVisibility(View.GONE);
            trVendor.setVisibility(View.GONE);

            AutoCompleteTextView acRegion = editUserDetail.findViewById(R.id.cre_ac_region);
            acRegion.setText(items.kota);
            acRegion.setInputType(InputType.TYPE_NULL);
            EditText etKci = (EditText)editUserDetail.findViewById(R.id.cre_et_KCI);
            etKci.setText(items.alamat);
            etKci.setInputType(InputType.TYPE_NULL);
            EditText etKcp = (EditText)editUserDetail.findViewById(R.id.cre_et_KCP);
            etKcp.setText(items.namaVendor);
            etKcp.setInputType(InputType.TYPE_NULL);
        }

        ((TextView)editUserDetail.findViewById(R.id.keterangan_pass)).setVisibility(View.INVISIBLE);

        etName.setText(items.namaLengkap);
        etName.setInputType(InputType.TYPE_NULL);
        etEmail.setText(items.email);
        etEmail.setInputType(InputType.TYPE_NULL);
        judul.setText("Detail User");


        editUserDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editUserDialog.dismiss();
            }
        });

        editUserDialog.show();
    }


    private void registerPOST(final String group, final String username, final String name, final String email,
                              final String password, final String vendor, final String alamat, final String kota) {
        String finalePrint = group + username + name + email + password + vendor + alamat + kota;
        Log.e("Daftar user", finalePrint);

        RequestQueue queue = SingletonRequestQueue.getInstance(getContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/authend/tb_users";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                pbCreateUser.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                pbUsers.setVisibility(View.VISIBLE);
                usersGET(rootView);
            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }

            @Override
            public Map getParams() {
                Map params = new HashMap();

                params.put("nama_vendor", vendor);
                params.put("nama_lengkap", name);
                params.put("email", email);
                params.put("kota", kota);
                params.put("alamat", alamat);
                params.put("grup", group);
                params.put("password", password);

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

    private void resetPassPOST(final String email) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/resetpass/tb_users";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }

            @Override
            public Map getParams() {
                Map params = new HashMap();

                params.put("email", email);
                params.put("password", "123456");

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

    private void deleteUserPOST(final String id) {
        RequestQueue queue = SingletonRequestQueue.getInstance(getContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = BuildConfig.BASE_URL + "/api/smallupdate";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                pbUsers.setVisibility(View.VISIBLE);
                usersGET(rootView);
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
                params.put("delete", "user");

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

}
