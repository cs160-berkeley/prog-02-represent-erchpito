package com.erchpito.represent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.erchpito.common.Representative;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class WatchListenerService extends WearableListenerService {

    private static final String TAG = "WatchListenerService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "in WatchListenerService, got: " + messageEvent.getPath());
        if(messageEvent.getPath().equalsIgnoreCase("/wearable_data")) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d(TAG, "about to start watch MainActivity with CAT_NAME: Fred");
            startActivity(intent);

        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        DataMap dataMap;

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (path.equals("/wearable_data")) {
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.v("myTag", "DataMap received on watch: " + dataMap);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("LOCATION", dataMap.getString("LOCATION"));
                    intent.putExtra("DISTRICT", dataMap.getString("DISTRCIT"));
                    ArrayList<DataMap> reps = dataMap.getDataMapArrayList("REPRESENTATIVES");
                    ArrayList<Representative> representatives = new ArrayList<Representative>();
                    for (DataMap rep : reps) {
                        representatives.add(new Representative(rep));
                    }
                    intent.putExtra("REPRESENTATIVES", representatives);
                    intent.putExtra("COUNTY", dataMap.getString("COUNTY"));
                    intent.putExtra("VOTES", dataMap.getStringArrayList("VOTES"));
                    startActivity(intent);
                }
            }
        }
    }
}
