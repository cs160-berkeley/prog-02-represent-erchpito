package com.erchpito.represent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.erchpito.common.Representative;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PhoneListenerService extends WearableListenerService {

    private static final String TAG = "PhoneListenerService";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        DataMap dataMap;

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (path.equals("/handheld_data_random")) {

                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.d(TAG, "DataMap received on phone: " + dataMap);

                    String zipcode = RepresentCalculator.findRandomZipCode();
                    String[] location = RepresentCalculator.findDistrict(zipcode).split("\n");
                    int color = RepresentCalculator.findColor(zipcode, this);
                    ArrayList<String> votes = RepresentCalculator.findVote(zipcode, 2012, this);
                    ArrayList<Representative> representatives = RepresentCalculator.findRepresentatives(zipcode, this);

                    Intent intent = new Intent(this, CongressionalActivity.class);
                    intent.putExtra("LOCATION", location[0]);
                    intent.putExtra("DISTRICT", location[1]);
                    intent.putExtra("REPRESENTATIVES", representatives);
                    intent.putExtra("COLOR", color);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    Intent serviceIntent = new Intent(this, PhoneToWatchService.class);
                    serviceIntent.putExtra("LOCATION", location[0]);
                    serviceIntent.putExtra("DISTRICT", location[1]);
                    serviceIntent.putExtra("REPRESENTATIVES", representatives);
                    serviceIntent.putExtra("COLOR", color);
                    serviceIntent.putExtra("VOTES", votes);
                    startService(serviceIntent);

                } else if (path.equals("/handheld_data_detailed")) {

                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.d(TAG, "DataMap received on phone: " + dataMap);

                    Intent intent = new Intent(this, DetailedActivity.class);
                    Representative rep = RepresentCalculator.findRepresentatives(dataMap.getString("ZIP"), this).get(dataMap.getInt("INDEX"));
                    intent.putExtra("REPRESENTATIVE", rep);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
    }
}