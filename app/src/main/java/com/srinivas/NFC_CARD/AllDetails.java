package com.srinivas.NFC_CARD;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AllDetails extends Activity {
    EditText id_et, vehicle_no, owner_name, owner_emp_id;

    EditText emp_id, emp_name, department_name, designation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_details);

        id_et = findViewById(R.id.id_et);
        vehicle_no = findViewById(R.id.vehicle_no);
        owner_name = findViewById(R.id.owner_name);
        owner_emp_id = findViewById(R.id.owner_emp_id);

        emp_id = findViewById(R.id.emp_id);
        emp_name = findViewById(R.id.emp_name);
        department_name = findViewById(R.id.department_name);
        designation = findViewById(R.id.designation);


        getIntent().getStringExtra("payload");
        try {
            fetchUser();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fetchUser() throws IOException {

        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("username", "admin1")
                .add("password", "primal@1")
                .build();
        final Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                //  .url("http://primal.reassuregroups.com/api/GetData/fetchUser/"+getIntent().getStringExtra("payload"))
                .url("http://primal.reassuregroups.com/api/GetData/fetchUser/e6432476328462")
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
                    Log.d("result", responseBody.toString());

                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        if (jsonObject.getJSONArray("data") instanceof JSONArray) {
                            // It's an array
                        } else {
                            System.out.println("Very good boy");
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject js = jsonArray.getJSONObject(i);
                            js.getString("empId");
                            js.getString("empnfcId");
                            js.getString("name");
                            js.getString("email");
                            js.getString("phone");
                            js.getString("designation");
                            js.getString("department");

                        }
                    } catch (JSONException e) {

                        try {
                            final JSONObject jsonObject = new JSONObject(responseBody);
                            System.out.println("Very good boy "+jsonObject.getString("success")+" msg "+jsonObject.getString("message"));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    try {
                                        showAlert(jsonObject.getString("message"));
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });


                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    public void showAlert(String msg) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AllDetails.this);
        LayoutInflater inflater = ((Activity) AllDetails.this).getLayoutInflater();
        View alertView = inflater.inflate(R.layout.warning_dialog, null);
        alertDialog.setView(alertView);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        final AlertDialog show = alertDialog.show();

        Button alertButton = (Button) alertView.findViewById(R.id.btn_ok);
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }


}
