package com.srinivas.NFC_CARD;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Home extends Activity implements View.OnClickListener {
    Button enroll_btn, login_btn;
    AlertDialog show;
    SharedPreferences validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        login_btn = findViewById(R.id.login_btn);
        enroll_btn = findViewById(R.id.enroll_btn);
        login_btn.setOnClickListener(this);
        enroll_btn.setOnClickListener(this);
        validate = getSharedPreferences("Validate", MODE_PRIVATE);

        try {
            if (validate.getString("validae", "done").equals("notdone")) {
                GetValidation();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                SharedPreferences gatedetals = getSharedPreferences("GATE", MODE_PRIVATE);
                if (validate.getString("validae", "done").equals("done")) {
                    if (gatedetals.getString("gateID", "").length() > 0) {
                        Intent vehicle = new Intent(Home.this, MainActivityView.class);
                        startActivity(vehicle);
                    } else {
                        Intent vehicle = new Intent(Home.this, GateLoginScan.class);
                        startActivity(vehicle);
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Please wait connecting to server", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.enroll_btn:
                if (validate.getString("validae", "done").equals("done")) {
                    Intent enroll = new Intent(Home.this, Login.class);
                    startActivity(enroll);
                } else {
                    Toast.makeText(getBaseContext(), "Please wait connecting to server", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void GetValidation() throws IOException {

        //https://docs.google.com/spreadsheets/d/1BWpOo_O_mBVV99TPdt5QdN7qzXlVVrbaE7dTQS3QhUs/edit#gid=0
        //emergencymail045@gmail.com password :  wadahell@123
        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .url("https://script.google.com/macros/s/AKfycbxC_st1dCob-DKbdwwnObhFFhr2KIMWJYk_XasQy87uYKQ_JQA/exec?" +
                        "id=1BWpOo_O_mBVV99TPdt5QdN7qzXlVVrbaE7dTQS3QhUs&sheet=nfc")
                .get()
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
                        Toast.makeText(getBaseContext(), "Please wait connecting to server", Toast.LENGTH_SHORT).show();

                    }
                });

                //pd.dismiss();
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                // pd.dismiss();
                if (!response.isSuccessful()) {

                    Log.d("result dadi", response.toString());
                    throw new IOException("Unexpected code " + response);
                } else {
                    //  pd.dismiss();
                    Log.d("result", response.toString());
                    String responseBody = response.body().string();
                    final JSONObject obj;
                    try {
                        JSONObject js = new JSONObject(responseBody.toString());
                        JSONArray records = new JSONArray(js.getString("records"));
                        Boolean result = false;
                        for (int i = 0; i < records.length(); i++) {
                            JSONObject res = records.getJSONObject(i);
                            System.out.println(res.toString());
                            if (res.getString("Facultyid").equals("admin")) {
                                result = true;
                                break;
                            } else {
                                result = false;
                            }
                        }
                        if (result) {
                            SharedPreferences.Editor validate = getSharedPreferences("Validate", MODE_PRIVATE).edit();
                            validate.putString("validae", "done");
                            validate.commit();

                        } else {
                            SharedPreferences.Editor validate = getSharedPreferences("Validate", MODE_PRIVATE).edit();
                            validate.putString("validae", "notdone");
                            validate.commit();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                     Toast.makeText(getBaseContext(), "Please wait connecting to server", Toast.LENGTH_SHORT).show();
                                    //showAlert("Your are not authorized user thanks !! ");
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void showAlert(String msg) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        LayoutInflater inflater = ((Activity) Home.this).getLayoutInflater();
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
