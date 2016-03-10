package com.erchpito.represent;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by erchpito on 10/3/2016.
 */
public final class APICallTask extends AsyncTask<String, Void, JSONObject> {

    private static final String TAG = "APICallTask";
    private static final String SUNLIGHT_API_KEY = "80e53d59d8394acabadf9a487bf502c6";
    private static final String GOOGLE_MAP_API_KEY = "80e53d59d8394acabadf9a487bf502c6";
    private static final String TWITTER_API_KEY = "80e53d59d8394acabadf9a487bf502c6";

    protected void onPreExecute() {
        ;
    }

    protected JSONObject doInBackground(String... params) {

        String call = params[0];
        if (call.contains("congress.api.sunlightfoundation.com")) {
            call += "&apikey=" + SUNLIGHT_API_KEY;
        } else if (call.contains("map.google.com")) {
            call += "&apikey=" + GOOGLE_MAP_API_KEY;
        } else if (call.contains("twitter.com")) {
            call += "&apikey=" + TWITTER_API_KEY;
        }

        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        JSONObject result = null;

        try {
            Log.d(TAG, "Calling " + call);

            url = new URL(call);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String temp, response = "";
            while ((temp = bufferedReader.readLine()) != null) {
                response += temp;
            }

            bufferedReader.close();
            urlConnection.disconnect();

            result = (JSONObject) new JSONTokener(response).nextValue();

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }

        return result;
    }

    protected void onProgress(Void... progress) {
        ;
    }

    protected void onPostExecute(JSONObject result) {
        ;
    }




}
