package com.daniml3.manager;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetUtils {

    public static JSONObject getJSONResponse(URL url) {
        try {
            JSONObject response;
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) stringBuilder.append(line);
            bufferedReader.close();

            response = new JSONObject(stringBuilder.toString());
            return response;
        } catch (IOException | JSONException e) {
            return new JSONObject();
        }
    }

    public static JSONObject getJSONResponse(String url) {
        try {
            return getJSONResponse(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
}
