package com.erchpito.represent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class WatchListenerService extends WearableListenerService {

    private static final String TAG = "WatchListenerService";

    private static final String FRED_FEED = "/Fred";
    private static final String LEXY_FEED = "/Lexy";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "in WatchListenerService, got: " + messageEvent.getPath());

        if(messageEvent.getPath().equalsIgnoreCase(FRED_FEED)) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("CAT_NAME", "Fred");
            Log.d(TAG, "about to start watch MainActivity with CAT_NAME: Fred");
            startActivity(intent);

        } else if (messageEvent.getPath().equalsIgnoreCase(LEXY_FEED)) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("CAT_NAME", "Lexy");
            Log.d(TAG, "about to start watch MainActivity with CAT_NAME: Lexy");
            startActivity(intent);

        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
