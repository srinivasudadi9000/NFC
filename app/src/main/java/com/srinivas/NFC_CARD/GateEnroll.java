package com.srinivas.NFC_CARD;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GateEnroll extends AppCompatActivity implements View.OnClickListener {
    TextView header_tv;
    EditText id_et, gate_name;
    ImageView save_img;
    Spinner gatetype_spinner;
    String gatetype;
    AlertDialog show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gate_enroll);
        gate_name = findViewById(R.id.gate_name);
        id_et = findViewById(R.id.id_et);
        save_img = findViewById(R.id.save_img);

        gatetype_spinner = findViewById(R.id.gatetype_spinner);
        gatetype_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gatetype = parent.getItemAtPosition(position).toString();
                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gatetype = "not";
            }
        });
        id_et.setText(getIntent().getStringExtra("payload").toString());
        save_img.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_img:
                try {
                    Getlogin(id_et.getText().toString(),id_et.getText().toString(),gatetype,gate_name.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void Getlogin(String gateID, String nfcid, String gateType, String name) throws IOException {

        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("gateID", gateID)
                .add("gateNFCID", nfcid)
                .add("gateType", gateType)
                .add("name", name)
                .build();
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .url("http://primal.reassuregroups.com/api/Enrollment/Gate?")
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
                    String responseBody = response.body().string();
                    showAlert(responseBody);
                    Log.d("result", responseBody.toString());
                    final JSONObject obj;

                }
            }
        });

    }

    public void showAlert(String msg) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(GateEnroll.this);
        LayoutInflater inflater = ((Activity) GateEnroll.this).getLayoutInflater();
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
