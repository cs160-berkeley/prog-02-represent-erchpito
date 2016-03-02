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
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("LOCATION", dataMap.getString("LOCATION"));
                    intent.putExtra("DISTRICT", dataMap.getString("DISTRICT"));
                    ArrayList<DataMap> reps = dataMap.getDataMapArrayList("REPRESENTATIVES");
                    ArrayList<Representative> representatives = new ArrayList<Representative>();
                    for (DataMap rep : reps) {
                        representatives.add(new Representative(rep));
                    }
                    intent.putExtra("REPRESENTATIVES", representatives);
                    intent.putExtra("COUNTY", dataMap.getString("COUNTY"));
                    intent.putExtra("VOTES", dataMap.getStringArrayList("VOTES"));
                    Log.d(TAG, dataMap.getString("DISTRICT"));
                    startActivity(intent);
                }
            }
        }
    }
}
