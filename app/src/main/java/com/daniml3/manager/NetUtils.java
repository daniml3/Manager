package com.daniml3.manager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class NetUtils {

    public static JSONObject getJSONResponse(URL url) {
        try {
            return new JSONObject(getResponse(url));
        } catch (JSONException e) {
            return new JSONObject();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static JSONObject getJSONResponse(String url) {
        try {
            return getJSONResponse(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponse(URL url) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            bufferedReader.close();

            return stringBuilder.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static String getResponse(String url) {
        try {
            return getResponse(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
