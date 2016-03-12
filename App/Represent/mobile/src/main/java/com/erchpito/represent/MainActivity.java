package com.erchpito.represent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.erchpito.common.Representative;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";

    private int mAnimationDuration;

    private Typeface font;

    private RelativeLayout mButtonField;

    private TextView mRepresentText;
    private EditText mZipEdit;
    private Button mZipButton;
    private Button mCurrentButton;

    private GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        font = Typeface.createFromAsset(getAssets(), "fonts/LeagueSpartan-Bold.otf");
        mAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

//        mButtonField = (RelativeLayout) findViewById(R.id.button_field);

        mRepresentText = (TextView) findViewById(R.id.represent_text);
        mRepresentText.setTypeface(font);

//        mZipButton = (Button) findViewById(R.id.zip_button);
//        mZipButton.setTypeface(font);

//        mCurrentButton = (Button) findViewById(R.id.current_button);
        mCurrentButton = (Button) findViewById(R.id.zip_current);
        mCurrentButton.setTypeface(font);

        mZipEdit = (EditText) findViewById(R.id.zip_edit);
        mZipEdit.setTypeface(font);
//        mZipEdit.setVisibility(View.GONE);
        mZipEdit.setFocusableInTouchMode(true);
        mZipEdit.setHintTextColor(ContextCompat.getColor(this, R.color.white));
        mZipEdit.requestFocus();
        mZipEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String zipcode = mZipEdit.getText().toString();
                    if (RepresentCalculator.verifyZipCode(zipcode, v.getContext())) {
                        toCongressionalView(RepresentCalculator.getLatLng(zipcode));
                    } else {
                        Log.d(TAG, "non-real postal code");
                        Toast.makeText(v.getContext(), "Unable to find postal code", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });

        mApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void enterLocation(View view) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mZipEdit.setAlpha(0f);
//                mZipEdit.setVisibility(View.VISIBLE);
//
//                mZipEdit.animate()
//                        .alpha(1f)
//                        .setDuration(mAnimationDuration)
//                        .setListener(null);
//
//                mButtonField.animate()
//                        .alpha(0f)
//                        .setDuration(mAnimationDuration)
//                        .setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                mButtonField.setVisibility(View.GONE);
//                            }
//                        });
//            }
//        }, 0);
        String zipcode = "94704";
        if (RepresentCalculator.verifyZipCode(zipcode, this)) {
            toCongressionalView(zipcode);
        }
        Log.d(TAG, "non-real postal code");
        Toast.makeText(this, "Unable to find postal code", Toast.LENGTH_LONG).show();
        return;

    }

    public void findLocation(View view) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
        if (mLastLocation != null) {
            toCongressionalView("" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
            return;
        }
        Log.d(TAG, "unable to get last location");
        Toast.makeText(this, "Unable to get location", Toast.LENGTH_LONG).show();
        return;
    }

    public void toCongressionalView(String latlng) {
        try {
            RepresentCalculator.getZipCode(latlng);
            String[] location = RepresentCalculator.findDistrict(latlng).split("\n");
            ArrayList<String> votes = RepresentCalculator.findVote(latlng, 2012, this);
            ArrayList<Representative> representatives = RepresentCalculator.findRepresentatives(latlng, this);
            int color = RepresentCalculator.findColor(latlng, this);

            Intent intent = new Intent(this, CongressionalActivity.class);
            intent.putExtra("LOCATION", location[0]);
            intent.putExtra("DISTRICT", location[1]);
            intent.putExtra("REPRESENTATIVES", representatives);
            intent.putExtra("COLOR", color);
            startActivity(intent);

            Intent serviceIntent = new Intent(this, PhoneToWatchService.class);
            serviceIntent.putExtra("LOCATION", location[0]);
            serviceIntent.putExtra("DISTRICT", location[1]);
            serviceIntent.putExtra("REPRESENTATIVES", representatives);
            serviceIntent.putExtra("COLOR", color);
            serviceIntent.putExtra("VOTES", votes);
            Log.d(TAG, "starting service");
            startService(serviceIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to retrieve data", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mApiClient.connect();
        mZipEdit.setText("");
    }

    @Override
    public void onStop() {
        super.onStop();
        mApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) { ; }

    @Override
    public void onConnectionSuspended(int cause) { ; }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { ; }
}
