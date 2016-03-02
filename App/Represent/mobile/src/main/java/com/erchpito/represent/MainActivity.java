package com.erchpito.represent;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.erchpito.common.Representative;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Typeface font;

    private Button mZipButton;
    private Button mCurrentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        font = Typeface.createFromAsset(getAssets(), "fonts/LeagueSpartan-Bold.otf");

        mZipButton = (Button) findViewById(R.id.zip_button);
        mZipButton.setTypeface(font);

        mCurrentButton = (Button) findViewById(R.id.current_button);
        mCurrentButton.setTypeface(font);
    }

    public void enterLocation(View view) {
        toCongressionalView(RepresentCalculator.findZipCode(0, 0));
    }

    public void findLocation(View view) {
        // make EditText view
        int zipcode = 94704;
        toCongressionalView(zipcode);
    }

    public void toCongressionalView(int zipcode) {
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
        Log.d(TAG, "starting service");
        startService(serviceIntent);
    }
}
