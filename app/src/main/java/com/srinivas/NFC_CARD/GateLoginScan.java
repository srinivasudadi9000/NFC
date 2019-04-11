package com.srinivas.NFC_CARD;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GateLoginScan extends Activity implements Listener {

    public static final String TAG = GateLoginScan.class.getSimpleName();

    private EditText mEtMessage;
    private Button mBtWrite;
    private Button mBtRead;

    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;

    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;

    private NfcAdapter mNfcAdapter;

    TextView msg_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msg_txt = findViewById(R.id.msg_txt);
        initViews();
        initNFC();

        Toast.makeText(getBaseContext(),"Place Your Gate Card Here ",Toast.LENGTH_SHORT).show();
    }

    private void initViews() {

        mEtMessage = (EditText) findViewById(R.id.et_message);
        mBtWrite = (Button) findViewById(R.id.btn_write);
        mBtRead = (Button) findViewById(R.id.btn_read);

        mBtWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GateLoginScan.this.showWriteFragment();
            }
        });
        mBtRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GateLoginScan.this.showReadFragment();

            }
        });
    }

    private void initNFC() {

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }


    private void showWriteFragment() {

        isWrite = true;

        mNfcWriteFragment = (NFCWriteFragment) getFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);

        if (mNfcWriteFragment == null) {

            mNfcWriteFragment = NFCWriteFragment.newInstance();
        }
        mNfcWriteFragment.show(getFragmentManager(), NFCWriteFragment.TAG);

    }

    private void showReadFragment() {

        mNfcReadFragment = (NFCReadFragment) getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);

        if (mNfcReadFragment == null) {

            mNfcReadFragment = NFCReadFragment.newInstance();
        }
        mNfcReadFragment.show(getFragmentManager(), NFCReadFragment.TAG);

    }

    @Override
    public void onDialogDisplayed() {

        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {

        isDialogDisplayed = false;
        isWrite = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        System.out.println("Chudra dadi " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: " + intent.getAction());

        if (tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);
            msg_txt.setText(bytesToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));


            if (!msg_txt.getText().toString().contains("payload")) {

                try {
                    gateUser(bytesToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getBaseContext(), "Already Scanned Card Thankyou ", Toast.LENGTH_SHORT).show();
            }
        }


    }


    public void showAlert(String msg) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GateLoginScan.this);
        LayoutInflater inflater = ((Activity) GateLoginScan.this).getLayoutInflater();
        View alertView = inflater.inflate(R.layout.warning_dialog, null);
        alertDialog.setView(alertView);
        alertDialog.setTitle("Gate Login Alert");
        alertDialog.setMessage(msg);
        final AlertDialog show = alertDialog.show();

        Button alertButton = (Button) alertView.findViewById(R.id.btn_ok);
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                Intent mainactivityview = new Intent(GateLoginScan.this, MainActivityView.class);
                startActivity(mainactivityview);
                finish();
            }
        });
    }


    public void gateUser(String tagid) throws IOException {

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
               // .url("http://primal.reassuregroups.com/api/GetData/Gate/1223")
                  .url("http://primal.reassuregroups.com/api/GetData/fetchUser/"+tagid)
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
                        final JSONObject jsonObject = new JSONObject(responseBody);
                        if (jsonObject.getJSONArray("data") instanceof JSONArray) {
                            // It's an array
                        } else {
                            System.out.println("Very good boy");
                        }
                        final JSONArray jsonArray = jsonObject.getJSONArray("data");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            final JSONObject js = jsonArray.getJSONObject(i);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    try {
                                        showAlert("gateID :" + js.getString("gateID") + "\ngateNFCID : " + js.getString("gateNFCID")
                                                + "\ngateType : " +
                                                js.getString("gateType"));
                                        SharedPreferences.Editor gatedetals = getSharedPreferences("GATE", MODE_PRIVATE).edit();
                                        gatedetals.putString("gateID", js.getString("gateID"));
                                        gatedetals.putString("gateNFCID", js.getString("gateNFCID"));
                                        gatedetals.putString("gateType", js.getString("gateType"));
                                        gatedetals.commit();


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        }
                    } catch (JSONException e) {

                        try {
                            final JSONObject jsonObject = new JSONObject(responseBody);
                            System.out.println("Very good boy " + jsonObject.getString("success") + " msg " + jsonObject.getString("message"));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    try {
                                        showAlert("Gate Login Mandatory "+ jsonObject.getString("message"));
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


}
