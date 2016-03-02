package com.erchpito.represent;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.erchpito.common.Representative;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WatchListenerService extends WearableListenerService {

    private static final String TAG = "WatchListenerService";

//    @Override
//    public void onMessageReceived(MessageEvent messageEvent) {
//        Log.d(TAG, "in WatchListenerService, got: " + messageEvent.getPath());
//        if(messageEvent.getPath().equalsIgnoreCase("/wearable_data")) {
//
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Log.d(TAG, "about to start watch MainActivity with CAT_NAME: Fred");
//            startActivity(intent);
//
//        }
//    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        DataMap dataMap;

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (path.equals("/wearable_data")) {
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.d(TAG, "DataMap received on watch: " + dataMap);
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

//        if (Log.isLoggable(TAG, Log.DEBUG)) {
//            Log.d(TAG, "onDataChanged: " + dataEvents);
//        }
//        final List events = FreezableUtils.freezeIterable(dataEvents);
//
//        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Wearable.API)
//                .build();
//
//        ConnectionResult connectionResult =
//                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);
//
//        if (!connectionResult.isSuccess()) {
//            Log.e(TAG, "Failed to connect to GoogleApiClient.");
//            return;
//        }

        // Loop through the events and send a message
        // to the node that created the data item.
//        for (DataEvent event : dataEvents) {
//            Log.d(TAG, "meow");
//            Uri uri = event.getDataItem().getUri();
//
//            // Get the node id from the host value of the URI
//            String nodeId = uri.getHost();
//            // Set the data of the message to be the bytes of the URI
//            byte[] payload = uri.toString().getBytes();
//
//            // Send the RPC
//            Wearable.MessageApi.sendMessage(googleApiClient, nodeId,
//                    DATA_ITEM_RECEIVED_PATH, payload);
//        }
    }
}
