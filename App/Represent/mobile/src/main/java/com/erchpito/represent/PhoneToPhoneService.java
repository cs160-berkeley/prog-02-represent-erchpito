package com.erchpito.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.erchpito.common.Representative;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class PhoneToPhoneService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "PhoneToPhoneService";

    private GoogleApiClient mApiClient;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "intent received");

        final DataMap DATAMAP = new DataMap();
        DATAMAP.putLong("TIME", System.currentTimeMillis());
        DATAMAP.putString("LATLNG", bundle.getString("LATLNG"));
        final String PATH = bundle.getString("ACTION");
        Log.d(TAG, "DataMap produced");

        new Thread(new Runnable() {
            @Override
            public void run() {
                mApiClient.connect();

                PutDataMapRequest putDMR = PutDataMapRequest.create("/handheld_data_" + PATH);
                putDMR.getDataMap().putAll(DATAMAP);
                PutDataRequest request = putDMR.asPutDataRequest();
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mApiClient, request).await();
                if (result.getStatus().isSuccess()) {
                    Log.d(TAG, "DataMap: " + DATAMAP + " sent successfully to data layer ");
                }
                else {
                    Log.d(TAG, "ERROR: failed to send DataMap to data layer");
                }
            }
        }).start();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();

        mApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Log.d(TAG, "service created");
        mApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) { ; }

    @Override
    public void onConnectionSuspended(int cause) { ; }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { ; }
}
