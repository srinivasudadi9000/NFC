package com.srinivas.NFC_CARD;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Login extends Activity implements View.OnClickListener {
    TextView header_tv;
    Button submit_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        header_tv = findViewById(R.id.header_tv);

        header_tv.setText(getIntent().getStringExtra("type").toString());
        SharedPreferences.Editor ss = getSharedPreferences("Details",MODE_PRIVATE).edit();
        ss.putString("type",getIntent().getStringExtra("type").toString());
        ss.commit();
        submit_btn = findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn:
                Intent submit_btn = new Intent(Login.this, MainActivity.class);
                startActivity(submit_btn);
                break;
        }
    }
}
