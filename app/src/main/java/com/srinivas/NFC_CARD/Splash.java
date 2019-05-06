package com.srinivas.NFC_CARD;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Splash extends Activity {
    LinearLayout myll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        myll = findViewById(R.id.myll);
        SharedPreferences.Editor validate = getSharedPreferences("Validate", MODE_PRIVATE).edit();
        validate.putString("validae", "notdone");
        validate.commit();
        myll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainaactivity = new Intent(Splash.this, Home.class);
                startActivity(mainaactivity);
            }
        });
        givepermissionaccess();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
            callintent();

        } else {
            //finish();
            Toast.makeText(getBaseContext(), "you can not use this application without givivng access to ur location Thanks!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void givepermissionaccess() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.NFC, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
           // Toast.makeText(getBaseContext(), "All permissions granted.", Toast.LENGTH_SHORT).show();
            callintent();

        }
    }

    public void callintent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash.this, Home.class);
                startActivity(i);
            }
        }, 2000);
    }

}
