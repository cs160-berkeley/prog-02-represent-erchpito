package com.erchpito.represent;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by erchpito on 10/3/2016.
 */
public final class APICallTask extends AsyncTask<String, Void, JSONObject> {

    private static final String TAG = "APICallTask";
    private static final String SUNLIGHT_API_KEY = "80e53d59d8394acabadf9a487bf502c6";
    private static final String GOOGLE_MAP_API_KEY = "AIzaSyAEuJ0TBnlbepU7WVHHw64mU9SiRyXwb-U";
    private static final String TWITTER_API_KEY = "80e53d59d8394acabadf9a487bf502c6";

    protected JSONObject doInBackground(String... params) {

        String call = params[0];
        if (call.contains("congress.api.sunlightfoundation.com")) {
            call += "&apikey=" + SUNLIGHT_API_KEY;
        } else if (call.contains("maps.googleapis.com")) {
            call += "&key=" + GOOGLE_MAP_API_KEY;
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
            Log.e(TAG, e.getMessage(), e);
            return null;
        }

        return result;
    }
}
