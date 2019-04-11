package com.srinivas.NFC_CARD;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Home extends Activity implements View.OnClickListener {
    Button enroll_btn, login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        login_btn = findViewById(R.id.login_btn);
        enroll_btn = findViewById(R.id.enroll_btn);
        login_btn.setOnClickListener(this);
        enroll_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                SharedPreferences gatedetals = getSharedPreferences("GATE", MODE_PRIVATE);
                if (gatedetals.getString("gateID","").length()>0){
                    Intent vehicle = new Intent(Home.this, MainActivityView.class);
                    startActivity(vehicle);
                }else {
                    Intent vehicle = new Intent(Home.this, GateLoginScan.class);
                    startActivity(vehicle);
                }

                break;
            case R.id.enroll_btn:
                Intent enroll = new Intent(Home.this, Login.class);
                startActivity(enroll);
                break;
        }
    }
}
