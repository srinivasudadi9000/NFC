package com.srinivas.NFC_CARD;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NFCReader extends Activity implements View.OnClickListener {
    Button enroll_btn, tagscan_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfcreader);
        tagscan_btn = findViewById(R.id.tagscan_btn);
        enroll_btn = findViewById(R.id.enroll_btn);

        enroll_btn.setOnClickListener(this);
        tagscan_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enroll_btn:

                Intent adminlogin = new Intent(NFCReader.this,Login.class);
                adminlogin.putExtra("type","Admin Login");
                startActivity(adminlogin);
                break;

            case R.id.tagscan_btn:
                Intent userlogin = new Intent(NFCReader.this,Login.class);
                userlogin.putExtra("type","User Login");
                startActivity(userlogin);
                break;
        }
    }
}
