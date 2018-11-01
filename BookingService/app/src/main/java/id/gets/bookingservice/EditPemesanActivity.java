package id.gets.bookingservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditPemesanActivity extends AppCompatActivity {

    String[] dataku;
    EditText namaPanjang, noHp, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpemesan);

         namaPanjang = (EditText)findViewById(R.id.edit_et_nama);
         noHp = (EditText)findViewById(R.id.edit_et_hp);
         email = (EditText)findViewById(R.id.edit_et_email);

        dataku = getIntent().getStringArrayExtra("data");
        if(dataku != null){
            for (String mydata: dataku) {
                Log.e("dieditpemesan", mydata);
                namaPanjang.setText(dataku[1]);
                email.setText(dataku[3]);
                noHp.setText(dataku[2]);
            }
        }

        Button selesai = (Button)findViewById(R.id.edit_btn_selesai);
        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                dataku[1] = namaPanjang.getText().toString();
                dataku[2] = noHp.getText().toString();
                dataku[3] = email.getText().toString();
                intent.putExtra("data", dataku);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });



    }
}
