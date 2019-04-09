package com.srinivas.NFC_CARD;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends Activity implements Listener {

    public static final String TAG = MainActivity.class.getSimpleName();

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
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent registration = new Intent(MainActivity.this, VehicleEnroll.class);
                registration.putExtra("payload", "dado");
                startActivity(registration);
            }
        }, 2000);
*/
    }

    private void initViews() {

        mEtMessage = (EditText) findViewById(R.id.et_message);
        mBtWrite = (Button) findViewById(R.id.btn_write);
        mBtRead = (Button) findViewById(R.id.btn_read);

        mBtWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.showWriteFragment();
            }
        });
        mBtRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.showReadFragment();

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
        System.out.println("Chudra dadi "+stringBuilder.toString());
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
                switch (getIntent().getStringExtra("type")) {
                    case "gate":

                        Intent gate = new Intent(MainActivity.this, GateEnroll.class);
                        gate.putExtra("payload", bytesToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                        startActivity(gate);
                        break;
                    case "vehicle":
                        Intent vehicle = new Intent(MainActivity.this, VehicleEnroll.class);
                        vehicle.putExtra("payload", bytesToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                        startActivity(vehicle);
                        break;
                    case "enroll_employ":
                        Intent enroll_employ = new Intent(MainActivity.this, EmployEnroll.class);
                        enroll_employ.putExtra("payload", bytesToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                        startActivity(enroll_employ);
                        break;

                }
                System.out.println("Dadi what is this " + tag.getId().toString());


                //System.out.println("Dadi seee here " + ndef.getTag().toString());
                //  System.out.println("Dadi seee tpe  " + ndef.getType().toString());
               /* System.out.println("Dadi seee getMaxSize " + ndef.getMaxSize());
                System.out.println("Dadi seee getCachedNdefMessage  " + ndef.getCachedNdefMessage().toString());
*/
            /*    Parcelable[] rawMessages =
                        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
               // System.out.println("Rawmessage lenght " + rawMessages.length);
                String x = "NFC Card Data Display : \n ";
                if (rawMessages != null) {
                    NdefMessage[] messages = new NdefMessage[rawMessages.length];
                    for (int i = 0; i < rawMessages.length; i++) {
                        messages[i] = (NdefMessage) rawMessages[i];
                        System.out.println("Seedadi ....." + messages[i].toString());
                        x = x + messages[i].toString();
                        msg_txt.setText(x);
                    }
                    System.out.println("Seedadi boabeee ....." + x.toString());
                    Intent registration = new Intent(MainActivity.this, VehicleEnroll.class);
                    registration.putExtra("payload", msg_txt.getText().toString());
                    startActivity(registration);
                }

                if (isDialogDisplayed) {

                    if (isWrite) {

                        String messageToWrite = mEtMessage.getText().toString();
                        mNfcWriteFragment = (NFCWriteFragment) getFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
                        mNfcWriteFragment.onNfcDetected(ndef, messageToWrite);

                    } else {

                        mNfcReadFragment = (NFCReadFragment) getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                        mNfcReadFragment.onNfcDetected(ndef);
                    }
                }*/
            } else {
                Toast.makeText(getBaseContext(), "Already Scanned Card Thankyou ", Toast.LENGTH_SHORT).show();
            }
        }


    }


    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

        return recordNFC;
    }

}
