package com.srinivas.NFC_CARD;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Login extends Activity implements View.OnClickListener {
    TextView header_tv;
    Button submit_btn;
    AlertDialog show;
    EditText username_et, password_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        submit_btn = findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(this);
        password_et = findViewById(R.id.password_et);
        username_et = findViewById(R.id.username_et);

        givepermissionaccess();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn:
                if (username_et.getText().toString().length() == 0){
                     Toast.makeText(getBaseContext(),"Please enter username ",Toast.LENGTH_SHORT).show();
                }else if (password_et.getText().toString().length() == 0){
                    Toast.makeText(getBaseContext(),"Please enter password ",Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        Getlogin();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                Intent submit_btn = new Intent(Login.this, MainActivity.class);
                //startActivity(submit_btn);
                break;
        }
    }

    /*
    *   HttpUrl.Builder urlBuilder = HttpUrl.parse("http://primal.reassuregroups.com/api/Enrollment/Login").newBuilder();
            urlBuilder.bod("username", "admin1");
            urlBuilder.addQueryParameter("password", "primal@1");


    * */

    public void showAlert(String msg) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
        LayoutInflater inflater = ((Activity) Login.this).getLayoutInflater();
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI

                        Intent alldetails = new Intent(Login.this, MainActivityView.class);
                        startActivity(alldetails);
                    }
                });


            }
        });
    }

    public void Getlogin() throws IOException {

        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                /* .add("username", "admin1")
                 .add("password", "primal@1")*/
                .add("username", username_et.getText().toString())
                .add("password", password_et.getText().toString())
                .build();
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .url("http://primal.reassuregroups.com/api/Enrollment/Login?")
                .post(formBody)
                .build();

        /*
         * http://125.62.194.181/patientapprovedqa/tracker.asmx/CheckRegistrationStatus?
         * key=r@t1ngsmd&NPIPhysicianID=3275830&IPAddress=192.168.232.2&DeviceIdentifier=e6e5037817642f9f
         * */
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
                    final JSONObject obj;
                    showAlert("Successfully Login Completed");

                }
            }
        });

    }

    public void givepermissionaccess() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED

        ) {
            Toast.makeText(getBaseContext(), "give permissin if condition ", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    0);
        } else {
            Toast.makeText(getBaseContext(), "All permissions granted.", Toast.LENGTH_SHORT).show();
//            givepermissionaccess();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
            //  givepermissionaccess();
            Toast.makeText(getBaseContext(), "done job", Toast.LENGTH_SHORT).show();
        } else {
            //finish();
            Toast.makeText(getBaseContext(), "you can not use this application without givivng access to ur location Thanks!!", Toast.LENGTH_SHORT).show();
        }
    }

}
