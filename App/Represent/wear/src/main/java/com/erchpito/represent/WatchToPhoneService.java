package com.erchpito.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.erchpito.common.Representative;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class WatchToPhoneService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "WatchToPhoneService";

    private GoogleApiClient mApiClient;

//    private List<Node> nodes = new ArrayList<>();
//
//    @Override //alternate method to connecting: no longer create this in a new thread, but as a callback
//    public void onConnected(Bundle bundle) {
//        Log.d(TAG, "in onconnected");
//        Wearable.NodeApi.getConnectedNodes(mApiClient)
//                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
//                    @Override
//                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
//                        nodes = getConnectedNodesResult.getNodes();
//                        Log.d(TAG, "found nodes");
//                        //when we find a connected node, we populate the list declared above
//                        //finally, we can send a message
//                        sendMessage("/handheld_data_random", "random");
//                        Log.d(TAG, "sent");
//                    }
//                });
//    }
//
//    private void sendMessage(final String path, final String text ) {
//        for (Node node : nodes) {
//            Wearable.MessageApi.sendMessage(
//                    mApiClient, node.getId(), path, text.getBytes());
//        }
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "intent received");

        final DataMap DATAMAP = new DataMap();
        DATAMAP.putLong("Time", System.currentTimeMillis());
        Log.d(TAG, "DataMap produced");

        new Thread(new Runnable() {
            @Override
            public void run() {
                mApiClient.connect();

                PutDataMapRequest putDMR = PutDataMapRequest.create("/handheld_data_random");
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
