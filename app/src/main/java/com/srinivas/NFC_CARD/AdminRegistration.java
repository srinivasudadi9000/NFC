package com.srinivas.NFC_CARD;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AdminRegistration extends Activity implements View.OnClickListener {
    TextView header_tv;
    EditText id_et, firstname_et, lastname_et, idnumber_et, address_et, department_et;
    ImageView save_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminregistration);
        id_et = findViewById(R.id.id_et);
        department_et = findViewById(R.id.department_et);
        firstname_et = findViewById(R.id.firstname_et);
        address_et = findViewById(R.id.address_et);
        idnumber_et = findViewById(R.id.idnumber_et);
        lastname_et = findViewById(R.id.lastname_et);
        header_tv = findViewById(R.id.header_tv);
        save_img = findViewById(R.id.save_img);
        save_img.setOnClickListener(this);
        SharedPreferences ss = getSharedPreferences("Details", MODE_PRIVATE);
        if (ss.getString("type", "").equals("Admin Login")) {
            id_et.setText(getIntent().getStringExtra("payload"));
            id_et.setEnabled(false);
        } else {
            header_tv.setText("Profile");
            id_et.setText("100000030303003030303");
            id_et.setEnabled(false);
            save_img.setVisibility(View.GONE);
            firstname_et.setText("Kholi");
            lastname_et.setText("V");
            idnumber_et.setText("00022");
            address_et.setText("Amaravathi");
            department_et.setText("STAFF");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_img:
                Toast.makeText(getBaseContext(), "Successfully Registration Completed Thankyou ", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
}
