package com.srinivas.NFC_CARD;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class VehicleEnroll extends Activity implements View.OnClickListener {
    TextView header_tv;
    EditText id_et, vehicle_no, owner_name, owner_emp_id;
    ImageView save_img;
    AlertDialog show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicleenroll);
        id_et = findViewById(R.id.id_et);
        vehicle_no = findViewById(R.id.vehicle_no);
        owner_name = findViewById(R.id.owner_name);
        owner_emp_id = findViewById(R.id.owner_emp_id);

        save_img = findViewById(R.id.save_img);
        save_img.setOnClickListener(this);
        id_et.setText(getIntent().getStringExtra("payload").toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_img:
                Toast.makeText(getBaseContext(), "Successfully Registration Completed Thankyou ", Toast.LENGTH_SHORT).show();
                //finish();
                try {
                    Getlogin(vehicle_no.getText().toString(), id_et.getText().toString(), owner_emp_id.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void Getlogin(String vehicleno, String nfcid, String empid) throws IOException {

        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("vehiclenumber", vehicleno)
                .add("vehicleNFCID", nfcid)
                .add("empid", empid)
                .build();
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .url("http://primal.reassuregroups.com/api/Enrollment/Vehicle?")
                .post(formBody)
                .build();


        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                //login.setVisibility(View.GONE);
                Log.d("result dadi", e.getMessage().toString());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        Toast.makeText(getBaseContext(), "IMEI Number or password doesnt exist", Toast.LENGTH_SHORT).show();
                    }
                });

                //pd.dismiss();
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                // pd.dismiss();
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Stuff that updates the UI
                            Toast.makeText(getBaseContext(), "IMEI Number or password doesnt exist", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.d("result dadi", response.toString());
                    throw new IOException("Unexpected code " + response);
                } else {
                    //  pd.dismiss();
                    final String responseBody = response.body().string();
                    Log.d("result", responseBody.toString());
                    final JSONObject obj;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Stuff that updates the UI
                            showAlert(responseBody);
                        }
                    });
                }
            }
        });

    }

    public void showAlert(String msg) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(VehicleEnroll.this);
        LayoutInflater inflater = ((Activity) VehicleEnroll.this).getLayoutInflater();
        View alertView = inflater.inflate(R.layout.warning_dialog, null);
        alertDialog.setView(alertView);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                show = alertDialog.show();

            }
        });


        Button alertButton = (Button) alertView.findViewById(R.id.btn_ok);
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }

}
