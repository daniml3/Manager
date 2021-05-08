package com.daniml3.manager;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

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
        String mBaseUrl = "https://server.danielml.ml/job/arrowos/buildWithParameters?token={token}&ARROW_ARGS={args}";
        StringBuilder mArgumentStringBuilder = new StringBuilder();

        for (String argument : arguments) {
            mArgumentStringBuilder.append(argument);
            mArgumentStringBuilder.append("%20");
        }


        return mBaseUrl.replace("{token}", token).replace("{args}", mArgumentStringBuilder.toString());
    }

    public static JSONObject getServerAvailableResponse() {
        return NetUtils.getJSONResponse("https://server.danielml.ml/computer/api/json");
    }

    public static JSONArray getJobList() {
        try {
            return Objects.requireNonNull(NetUtils
                    .getJSONResponse("https://server.danielml.ml/api/json?depth=3")).getJSONArray("jobs").getJSONObject(0).getJSONArray("builds");
        } catch (JSONException | NullPointerException e) {
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
