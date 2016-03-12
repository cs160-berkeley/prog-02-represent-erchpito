package com.erchpito.represent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by erchpito on 12/3/2016.
 */
public class APICallImageTask extends AsyncTask<String, Void, Bitmap>{

    private static final String TAG = "APICallImageTask";
    private static final String GOOGLE_MAP_API_KEY = "AIzaSyAEuJ0TBnlbepU7WVHHw64mU9SiRyXwb-U";

    protected Bitmap doInBackground(String... params) {

        String call = params[0];
        if (call.contains("maps.googleapis.com")) {
            call += "&key=" + GOOGLE_MAP_API_KEY;
        }

        URL url = null;
        HttpURLConnection urlConnection = null;
        Bitmap result = null;

        try {
            Log.d(TAG, "Calling " + call);
            url = new URL(call);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream input = urlConnection.getInputStream();
            result = BitmapFactory.decodeStream(input);

            input.close();
            urlConnection.disconnect();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }

        return result;
    }
}
