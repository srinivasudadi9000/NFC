package com.srinivas.NFC_CARD;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivityView extends Activity implements Listener {

    public static final String TAG = MainActivityView.class.getSimpleName();

    private EditText mEtMessage;
    private Button mBtWrite;
    private Button mBtRead;
    private ImageView logout;
    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;

    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;

    private NfcAdapter mNfcAdapter;

    TextView msg_txt,gateid_name;
    AlertDialog show;
    ProgressDialog progressdilaog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        msg_txt = findViewById(R.id.msg_txt);
        gateid_name = findViewById(R.id.gateid_name);
        logout = findViewById(R.id.logout);
        SharedPreferences gatedetals = getSharedPreferences("GATE", MODE_PRIVATE);
        gateid_name.setText("Gate ID "+gatedetals.getString("gateNFCID", "")+
                "\n Gate Type : "+gatedetals.getString("gateType",""));
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor gatedetals = getSharedPreferences("GATE", MODE_PRIVATE).edit();
                gatedetals.putString("gateID", "");
                gatedetals.putString("gateNFCID", "");
                gatedetals.putString("gateType","");
                gatedetals.commit();
                Intent home = new Intent(MainActivityView.this,Home.class);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(home);
                finish();
            }
        });
        initViews();
        initNFC();
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent registration = new Intent(MainActivity.this, VehicleEnroll.class);
                registration.putExtra("payload", "dado");
                startActivity(registration);
            }
        }, 2000);
*/

        // showAlert("dadi working here");
       /* try {
            fetchUser();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private void initViews() {

        mEtMessage = (EditText) findViewById(R.id.et_message);
        mBtWrite = (Button) findViewById(R.id.btn_write);
        mBtRead = (Button) findViewById(R.id.btn_read);

        mBtWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivityView.this.showWriteFragment();
            }
        });
        mBtRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivityView.this.showReadFragment();

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

            if (msg_txt.getText().toString().length()>0) {

                try {
                    fetchUser(bytesToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getBaseContext(), "Already Scanned Card Thankyou ", Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void showLog(String msg) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivityView.this);
        LayoutInflater inflater = ((Activity) MainActivityView.this).getLayoutInflater();
        View alertView = inflater.inflate(R.layout.warning_dialog, null);
        alertDialog.setView(alertView);
        alertDialog.setTitle("Log Alert");
        alertDialog.setMessage(msg);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
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

    public void showAlert(String msg, final String tagid, final String type) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivityView.this);
        LayoutInflater inflater = ((Activity) MainActivityView.this).getLayoutInflater();
        View alertView = inflater.inflate(R.layout.warning_dialog, null);
        alertDialog.setView(alertView);
        alertDialog.setTitle("Employee / Vehicle Alert");
        alertDialog.setMessage(msg);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
                show = alertDialog.show();
            }
        });


        Button alertButton = (Button) alertView.findViewById(R.id.btn_ok);
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                try {
                    progressdilaog = new ProgressDialog(MainActivityView.this);
                    progressdilaog.setTitle("");
                    progressdilaog.setMessage("Please wait");
                    progressdilaog.setCancelable(false);
                    progressdilaog.show();

                    if (!type.equals("")){
                        Getlogin(tagid,type);
                    }else {
                        progressdilaog.dismiss();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }


            }
        });
    }


    public void fetchUser(final String tagid) throws IOException {
        progressdilaog = new ProgressDialog(MainActivityView.this);
        progressdilaog.setTitle("");
        progressdilaog.setMessage("Please wait");
        progressdilaog.setCancelable(false);
        progressdilaog.show();
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
                // .url("http://primal.reassuregroups.com/api/GetData/fetchUser/e643247632846333")
                .url("http://primal.reassuregroups.com/api/GetData/fetchUser/" + tagid)
                .get()
                .build();


        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                //login.setVisibility(View.GONE);
                progressdilaog.dismiss();
                Log.d("result dadi", e.getMessage().toString());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        Toast.makeText(getBaseContext(), "Please try again!!", Toast.LENGTH_SHORT).show();
                    }
                });

                //pd.dismiss();
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                progressdilaog.dismiss();
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
                                   /* try {
                                        Getlogin(tagid);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }*/
                                    msg_txt.setText("");
                                    try {
                                        if (js.getString("type").equals("2")) {
                                            showAlert("EmpId :" + js.getString("empid") + "\nVehicleNO : " + js.getString("vehiclenumber")
                                                    + "\nVehicleNFCID : " +
                                                    js.getString("vehicleNFCID"), tagid,"2");

                                        } else {
                                            showAlert("EmpId :" + js.getString("empId") + "\nEmpNFCID : " + js.getString("empnfcId")
                                                    + "\nName : " +
                                                    js.getString("name") + " \nEmai : l" + js.getString("email") + "\nPhone : " +
                                                    js.getString("phone") + "\nDesignation : " + js.getString("designation") +
                                                    "\nDepartment : " + js.getString("department"), tagid,"1");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        }
                    } catch (JSONException e) {
                        try {
                            final JSONObject jsonObject = new JSONObject(responseBody);
                            System.out.println("Very good boy " + jsonObject.getString("success") + " msg " +
                                    jsonObject.getString("message"));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Stuff that updates the UI

                                    try {
                                        showAlert(jsonObject.getString("message"), "","");
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


    public void Getlogin(String tag,String type) throws IOException {
        SharedPreferences gatedetals = getSharedPreferences("GATE", MODE_PRIVATE);

        // avoid creating several instances, should be singleon
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("gatenfcid", gatedetals.getString("gateID", ""))
                .add("nfcid", tag)
                .add("type", type)
                .build();
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .url("http://primal.reassuregroups.com/api/GetData/TableReport?")
                .post(formBody)
                .build();

        System.out.println("Table reprot "+tag+" type "+gatedetals.getString("gateType",""));

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                //login.setVisibility(View.GONE);
                progressdilaog.dismiss();
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
                progressdilaog.dismiss();
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
                    showLog(responseBody.toString());
                }
            }
        });

    }


}
