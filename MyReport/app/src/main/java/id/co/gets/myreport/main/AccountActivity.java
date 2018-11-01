package id.co.gets.myreport.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import id.co.gets.myreport.BuildConfig;
import id.co.gets.myreport.R;
import id.co.gets.myreport.dbhelper.DbHelper;
import id.co.gets.myreport.dbhelper.FeedReaderContract;
import id.co.gets.myreport.network.SingletonRequestQueue;

public class AccountActivity extends AppCompatActivity {

    TextInputLayout tiUsername, tiCompany, tiEmail, tiPhone, tiAddress, tiFullname;
    TextInputEditText etUsername, etCompany, etEmail, etPhone, etAddress, etFullname;
    TextView txtUsername, txtCompany, btnSimpan;
    ImageView profile;
    DbHelper mDbHelper;
    ImageButton edit;
    ProgressBar pbAccount;

    byte[] byteImage1;
    Bitmap bp;

    String myId;
    String[] dataUser;

    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);


        profile = findViewById(R.id.profilepict);


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AccountActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AccountActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            SELECT_PICTURE);
                } else {

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            SELECT_PICTURE);
                }
            }
        });



        edit = findViewById(R.id.acc_btn_edit);
        btnSimpan = findViewById(R.id.acc_btn_save);
        btnSimpan.setVisibility(View.GONE);
        pbAccount = findViewById(R.id.pb_acc);
        pbAccount.setVisibility(View.GONE);

        txtUsername = findViewById(R.id.acc_txt_username);
        txtCompany = findViewById(R.id.acc_txt_company);

        tiUsername = findViewById(R.id.acc_ti_username);
        tiFullname = findViewById(R.id.acc_ti_nama);
        tiCompany = findViewById(R.id.acc_ti_company);
        tiEmail = findViewById(R.id.acc_ti_email);
        tiPhone = findViewById(R.id.acc_ti_phone);
        tiAddress = findViewById(R.id.acc_ti_address);

        etUsername = findViewById(R.id.acc_et_username);
        etFullname = findViewById(R.id.acc_et_nama);
        etCompany = findViewById(R.id.acc_et_company);
        etEmail = findViewById(R.id.acc_et_email);
        etPhone = findViewById(R.id.acc_et_phone);
        etAddress = findViewById(R.id.acc_et_address);

        etUsername.setInputType(InputType.TYPE_NULL);
        etFullname.setInputType(InputType.TYPE_NULL);
        etCompany.setInputType(InputType.TYPE_NULL);
        etEmail.setInputType(InputType.TYPE_NULL);
        etPhone.setInputType(InputType.TYPE_NULL);
        etAddress.setInputType(InputType.TYPE_NULL);

        mDbHelper = new DbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM entry", null);
        cursor.moveToFirst();
        myId = "";
        String rawUser = "";

        if (cursor.moveToFirst()) {
            do {
                myId = cursor.getString(0);
                rawUser = cursor.getString(cursor.getColumnIndex("user"));
            } while (cursor.moveToNext());
        }
        cursor.close();

        Cursor cursor1 = db.rawQuery("SELECT * FROM entry2", null);
        cursor1.moveToFirst();

        if (cursor1.moveToFirst()) {
                byteImage1 = cursor1.getBlob(cursor1.getColumnIndex("profile"));
        }
        cursor1.close();


        if(byteImage1 != null) {
            profile.setImageBitmap(BitmapFactory.decodeByteArray(byteImage1, 0, byteImage1.length));
        }

        dataUser = rawUser.split("&&");

        txtUsername.setText(dataUser[2]);
        txtCompany.setText(dataUser[3]);
        etUsername.setText(dataUser[2]);
        etFullname.setText(dataUser[4]);
        etCompany.setText(dataUser[3]);
        etEmail.setText(dataUser[5]);
        etPhone.setText(dataUser[6]);
        etAddress.setText(dataUser[7]);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUsername.setInputType(InputType.TYPE_CLASS_TEXT);
                etFullname.setInputType(InputType.TYPE_CLASS_TEXT);
                etCompany.setInputType(InputType.TYPE_CLASS_TEXT);
                etEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                etPhone.setInputType(InputType.TYPE_CLASS_PHONE);
                etAddress.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                btnSimpan.setVisibility(View.VISIBLE);
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbAccount.setVisibility(View.VISIBLE);
                postEditUser();
                Log.e("Getusername", etUsername.getText().toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SELECT_PICTURE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            SELECT_PICTURE);

                } else {
                   Toast.makeText(AccountActivity.this, "Anda tidak dapat mengupload gambar",
                           Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM entry2", null);
        boolean isAvailable = cursor.moveToFirst();

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if(selectedImageUri !=null && !isAvailable){
                    bp = decodeUri(selectedImageUri, 400);
                    profile.setVisibility(View.VISIBLE);
                    profile.setImageBitmap(bp);

                    byteImage1 = profileImage(bp);
                    db = mDbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(FeedReaderContract.FeedEntry._ID, myId);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_PROFILE_PICT, byteImage1);
                    db.insert(FeedReaderContract.FeedEntry.TABLE2_NAME, null, values);

                } else if(selectedImageUri !=null && isAvailable ){
                    bp = decodeUri(selectedImageUri, 400);
                    profile.setVisibility(View.VISIBLE);
                    profile.setImageBitmap(bp);

                    byteImage1 = profileImage(bp);
                    db = mDbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(FeedReaderContract.FeedEntry.COLUMN_PROFILE_PICT, byteImage1);
                    db.update(FeedReaderContract.FeedEntry.TABLE2_NAME, values,"_ID = ?", new String[]{myId});
                }
            }
        }

        cursor.close();
    }


    protected Bitmap decodeUri(Uri selectedImage, int REQUIRED_SIZE) {

        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

            // The new size we want to scale to
            // final int REQUIRED_SIZE =  size;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //Convert bitmap to bytes
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private byte[] profileImage(Bitmap b){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 0, bos);
        return bos.toByteArray();
    }

    Response.ErrorListener errorListener = new
            Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error instanceof NetworkError) {
                        Toast.makeText(getApplicationContext(), "No network available", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };

    private void postEditUser() {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;

        String uri = BuildConfig.BASE_URL + "api/updateuser";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.wtf(response, "utf-8");
                Log.e("SaveUserResult", response);
                pbAccount.setVisibility(View.INVISIBLE);
                btnSimpan.setVisibility(View.GONE);

                if(response.contains("berhasil")){
                    StringBuilder sb = new StringBuilder();
                    dataUser[2] = etUsername.getText().toString();
                    dataUser[3] = etCompany.getText().toString();
                    dataUser[4] = etFullname.getText().toString();
                    dataUser[5] = etEmail.getText().toString();
                    dataUser[6] = etPhone.getText().toString();
                    dataUser[7] = etAddress.getText().toString();

                    for (int j = 0; j < dataUser.length; j++) {
                        sb.append(dataUser[j]).append("&&");
                    }

                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(FeedReaderContract.FeedEntry.COLUMN_USER_DATA, sb.toString());
                    db.update(FeedReaderContract.FeedEntry.TABLE_NAME, values,"_ID = ?", new String[]{myId});

                    Toast.makeText(AccountActivity.this, "perubahan berhasil disimpan", Toast.LENGTH_SHORT).show();

                    etUsername.setInputType(InputType.TYPE_NULL);
                    etFullname.setInputType(InputType.TYPE_NULL);
                    etCompany.setInputType(InputType.TYPE_NULL);
                    etEmail.setInputType(InputType.TYPE_NULL);
                    etPhone.setInputType(InputType.TYPE_NULL);
                    etAddress.setInputType(InputType.TYPE_NULL);
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

                params.put("id_users", myId);
                params.put("username", etUsername.getText().toString());
                params.put("nama_vendor", etCompany.getText().toString());
                params.put("nama_lengkap", etFullname.getText().toString());
                params.put("email", etEmail.getText().toString());
                params.put("no_telp", etPhone.getText().toString());
                params.put("alamat", etAddress.getText().toString());
                params.put("kota", dataUser[8]);
                params.put("update", "enduser");

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

}
