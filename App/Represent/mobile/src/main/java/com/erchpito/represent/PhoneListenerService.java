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

//    @Override
//    public void onMessageReceived(MessageEvent messageEvent) {
//        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
//        if( messageEvent.getPath().equalsIgnoreCase("/handheld_data_random") ) {
//
//            int zipcode = RepresentCalculator.findRandomZipCode();
//            String location = RepresentCalculator.findLocation(zipcode);
//            String district = RepresentCalculator.findCongressionalDistrict(zipcode);
//            String county = RepresentCalculator.findCounty(zipcode);
//            ArrayList<String> votes = RepresentCalculator.find2012Vote(zipcode);
//            ArrayList<Representative> representatives = RepresentCalculator.findRepresentatives(zipcode);
//
//            Intent intent = new Intent(this, CongressionalActivity.class);
//            intent.putExtra("LOCATION", location);
//            intent.putExtra("DISTRICT", district);
//            intent.putExtra("REPRESENTATIVES", representatives);
//            startActivity(intent);
//
//            Intent serviceIntent = new Intent(this, PhoneToWatchService.class);
//            serviceIntent.putExtra("LOCATION", location);
//            serviceIntent.putExtra("DISTRICT", district);
//            serviceIntent.putExtra("REPRESENTATIVES", representatives);
//            serviceIntent.putExtra("COUNTY", county);
//            serviceIntent.putExtra("VOTES", votes);
//            startService(serviceIntent);
//
//        } else {
//            super.onMessageReceived( messageEvent );
//        }
//
//    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        DataMap dataMap;

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (path.equals("/handheld_data_random")) {

                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.d(TAG, "DataMap received on watch: " + dataMap);

                    int zipcode = RepresentCalculator.findRandomZipCode();
                    String location = RepresentCalculator.findLocation(zipcode);
                    String district = RepresentCalculator.findCongressionalDistrict(zipcode);
                    String county = RepresentCalculator.findCounty(zipcode);
                    ArrayList<String> votes = RepresentCalculator.find2012Vote(zipcode);
                    ArrayList<Representative> representatives = RepresentCalculator.findRepresentatives(zipcode);

                    Intent intent = new Intent(this, CongressionalActivity.class);
                    intent.putExtra("LOCATION", location);
                    intent.putExtra("DISTRICT", district);
                    intent.putExtra("REPRESENTATIVES", representatives);
                    startActivity(intent);

                    Intent serviceIntent = new Intent(this, PhoneToWatchService.class);
                    serviceIntent.putExtra("LOCATION", location);
                    serviceIntent.putExtra("DISTRICT", district);
                    serviceIntent.putExtra("REPRESENTATIVES", representatives);
                    serviceIntent.putExtra("COUNTY", county);
                    serviceIntent.putExtra("VOTES", votes);
                    startService(serviceIntent);

                } else if (path.equals("/handheld_data_detailed")) {

//                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
//                    Log.d(TAG, "DataMap received on watch: " + dataMap);
//                    Intent intent = new Intent(this, DetailedActivity.class);
//                    intent.putExtra("REPRESENTATIVE", rep);
//                    startActivity(intent);
                }
            }
        }
    }
}