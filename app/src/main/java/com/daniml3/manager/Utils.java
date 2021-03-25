package com.daniml3.manager;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Utils {

    public static void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignore) {
        }
    }

    public static String getSchedulingUrl(String token, ArrayList<String> arguments) {
        String mBaseUrl = "https://server.danielml.ml/job/arrowos/buildWithParameters?token={token}";
        StringBuilder mArgumentStringBuilder = new StringBuilder();

        for (int i = 0; i < arguments.size() - 1; i++) {
            String mArgumentName = arguments.get(i);
            String mArgumentValue = arguments.get(i + 1);

            mArgumentStringBuilder.append("&{name}={value}"
                    .replace("{name}", mArgumentName)
                    .replace("{value}", mArgumentValue));
        }

        return mBaseUrl.replace("{token}", token) + mArgumentStringBuilder.toString();
    }

    public static JSONObject getServerAvailableResponse() {
        return NetUtils.getJSONResponse("https://server.danielml.ml/computer/api/json");
    }

    public static JSONArray getJobList() {
        try {
            return NetUtils
                    .getJSONResponse("https://server.danielml.ml/api/json?depth=3").getJSONArray("jobs").getJSONObject(0).getJSONArray("builds");
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public static boolean isServerAvailable() {
        JSONObject response = getServerAvailableResponse();
        return !(response == null);
    }

    public static String firstLetterToUpperCase(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
