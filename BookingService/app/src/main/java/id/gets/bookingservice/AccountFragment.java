package id.gets.bookingservice;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Map;


public class AccountFragment extends Fragment{

    private View rootView;
    String[] data;
    EditText eNama, eNohp, eEmail, eAlamat;
    String nama, noHp, email, alamat, username, id, myId;
    DbHelper mDbHelper;
    protected Cursor cursor;
    ProgressBar progressBar;
    ContentValues values = new ContentValues();

    TextView tUser;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        if (rootView ==null) {
            rootView = inflater.inflate(R.layout.fragment_account, container, false);
        }
        mDbHelper = new DbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM entry", null);
        cursor.moveToFirst();
        String myUser = "";
        myId = "";
        if (cursor.moveToFirst()) {
            do {
                myId = cursor.getString(0);
                myUser = cursor.getString(cursor.getColumnIndex("user"));
            } while (cursor.moveToNext());
        }

        eNama = rootView.findViewById(R.id.acc_nama);
        eNohp = rootView.findViewById(R.id.acc_no_hp);
        eEmail = rootView.findViewById(R.id.acc_email);
        eAlamat = rootView.findViewById(R.id.acc_alamat);
        tUser = rootView.findViewById(R.id.txt_acc_username);
        progressBar = rootView.findViewById(R.id.acc_bar);
        progressBar.setVisibility(View.INVISIBLE);

        data = myUser.split("-");

        id = data[0];
        nama = data[1];
        noHp = data[2];
        email = data[3];
        alamat = data[4];
        username = data[6];

        if(nama.equals("null") || noHp.equals("null") || alamat.equals("null")){
            nama = ""; noHp=""; alamat="";
        }


        eNama.setText(nama);
        eNohp.setText(noHp);
        eEmail.setText(email);
        eAlamat.setText(alamat);
        tUser.setText(username);

        Button logout = (Button)rootView.findViewById(R.id.btn_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toLogin = new Intent(getActivity(), LoginActivity.class);
                toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                toLogin.putExtra("isLogedout", true);

                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.execSQL("delete from entry where _ID = '" + myId + "'");

                getActivity().finishAndRemoveTask();
                startActivity(toLogin);

            }
        });

        Button save = (Button)rootView.findViewById(R.id.acc_btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                POSTRequest();
            }
        });

        return rootView;

    }

    private void POSTRequest() {
        RequestQueue queue = SingletonRequestQueue.getInstance(getContext()).getRequestQueue();
        VolleyLog.DEBUG = true;

        String uri = NetworksUtil.BASE_URL + "api/updateuser";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                Log.e("SaveUserResult", response);
                progressBar.setVisibility(View.INVISIBLE);

                if(response.contains("berhasil")){
                    StringBuilder sb = new StringBuilder();
                    data[1] = eNama.getText().toString();
                    data[2] = eNohp.getText().toString();
                    data[3] = eEmail.getText().toString();
                    data[4] = eAlamat.getText().toString();

                    for (int j = 0; j < data.length; j++) {
                        sb.append(data[j]).append("-");
                    }

                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    values.put(FeedReaderContract.FeedEntry.COLUMN_USER, sb.toString());
                    db.update(FeedReaderContract.FeedEntry.TABLE_NAME, values,"_ID = ?", new String[]{data[0]});

                    Toast.makeText(getContext(), "perubahan berhasil disimpan", Toast.LENGTH_SHORT).show();
                }


            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map params = new HashMap();

                params.put("id", id);
                params.put("nama_lengkap", eNama.getText().toString());
                params.put("email", eEmail.getText().toString());
                params.put("alamat", eAlamat.getText().toString());
                params.put("noHp", eNohp.getText().toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                return  headers;
            }
        };

        queue.add(stringRequest);
    }

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error instanceof NetworkError) {
                Toast.makeText(getContext(), "No network available", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };


}
