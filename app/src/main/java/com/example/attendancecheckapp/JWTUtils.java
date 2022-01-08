package com.example.attendancecheckapp;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class JWTUtils {
    private static String header;
    private static String body;

    public static void decoded(String JWTEncoded) throws Exception {
        try {
            String[] split = JWTEncoded.split("\\.");
            header = getJson(split[0]);
            body = getJson(split[1]);
            Log.d("JWT_DECODED", "Header: " + header);
            Log.d("JWT_DECODED", "Body: " + body);
        } catch (UnsupportedEncodingException e) {
            //Error
        }
    }

    public static String getPayload(String key) throws JSONException {
        JSONObject jsonObject = new JSONObject(body);
        Log.d("JWT_DECODED", "id: " + jsonObject.get("id"));
        return String.valueOf(jsonObject.get(key));
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
