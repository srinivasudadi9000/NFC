package com.srinivas.NFC_CARD.model;

import java.util.HashMap;

public class NFCProtocol {
    private static HashMap<Integer, String> protMap = new HashMap<>();

    static {
        protMap.put(0x01, "http://www.");
        protMap.put(0x02, "https://www.");
        protMap.put(0x03, "http://");
        protMap.put(0x04, "https://www.");
        protMap.put(0x05, "tel:");
        protMap.put(0x06, "mailto:");
        protMap.put(0x07, "ftp://anonymous:anonymous@");
        protMap.put(0x08, "ftp://ftp.");
        protMap.put(0x09, "ftps://");
    }

    public static String getProtocol(int code) {
        return protMap.get(code);
    }

}