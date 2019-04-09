package com.srinivas.NFC_CARD;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Enrollments extends Activity implements View.OnClickListener {
    Button enroll_vehicle, enroll_employ, enroll_gate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enrollments);
        enroll_vehicle = findViewById(R.id.enroll_vehicle);
        enroll_employ = findViewById(R.id.enroll_employ);
        enroll_gate = findViewById(R.id.enroll_gate);
        enroll_vehicle.setOnClickListener(this);
        enroll_employ.setOnClickListener(this);
        enroll_gate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enroll_gate:
                Intent gate = new Intent(Enrollments.this, MainActivity.class);
                gate.putExtra("type", "gate");
                startActivity(gate);
                break;
            case R.id.enroll_vehicle:
                Intent vehicle = new Intent(Enrollments.this, MainActivity.class);
                vehicle.putExtra("type", "vehicle");
                startActivity(vehicle);
                break;
            case R.id.enroll_employ:
                Intent enroll_employ = new Intent(Enrollments.this, MainActivity.class);
                enroll_employ.putExtra("type", "enroll_employ");
                startActivity(enroll_employ);
                break;
        }
    }
}
