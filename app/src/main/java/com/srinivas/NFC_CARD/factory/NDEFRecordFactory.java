package com.srinivas.NFC_CARD.factory;
import android.nfc.NdefRecord;
import android.util.Log;

import com.srinivas.NFC_CARD.model.BaseRecord;
import com.srinivas.NFC_CARD.model.NDEFExternalType;
import com.srinivas.NFC_CARD.model.RDTSpRecord;
import com.srinivas.NFC_CARD.model.RDTTextRecord;

import java.util.Arrays;

public class NDEFRecordFactory {
    public static BaseRecord createRecord(NdefRecord record) {
        short tnf = record.getTnf();
        byte[] cont = record.getPayload();

        Log.d("Nfc", "Dump record ["+dumpPayload(record.getPayload())+"]");

        if (tnf == NdefRecord.TNF_WELL_KNOWN) {
            Log.d("Nfc", "Well Known");
            // Check if it is TEXT
            if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                RDTTextRecord result = RDTTextRecord.createRecord(record.getPayload());
                return result;
            }
            else if (Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
                Log.d("Nfc", "RTD_URI");
                //Log.d("Nfc", "Content [" + new String(data.content)+ "]");
            }
            else if (Arrays.equals(record.getType(), NdefRecord.RTD_SMART_POSTER)) {
                Log.d("Nfc", "Smart poster");


                //Log.d("Nfc", "Smart poster ["+new String(data.content)+"]");
                RDTSpRecord result = RDTSpRecord.createRecord(record.getPayload());
                return result;
            }
            // Maybe handle more
        }
        else if (tnf == NdefRecord.TNF_EXTERNAL_TYPE) {

            NDEFExternalType extType = NDEFExternalType.createRecord(record.getPayload());
        }

        return null;
    }



    private static String dumpPayload(byte[] payload) {
        StringBuffer pCont = new StringBuffer();
        for (int rn=0; rn < payload.length;rn++) {
            pCont.append(" " + ( Integer.toHexString(payload[rn])));
        }

        return pCont.toString();
    }

    private static String dumpPayload2String(byte[] payload) {
        StringBuffer pCont = new StringBuffer();
        for (int rn=0; rn < payload.length;rn++) {
            pCont.append(( char) payload[rn]);
        }

        return pCont.toString();
    }
}
