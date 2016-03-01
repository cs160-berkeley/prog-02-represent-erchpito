package com.erchpito.represent;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.erchpito.common.Representative;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Typeface font;
    private int mLocation;
    private String mDistrict;
    private String mCounty;
    private ArrayList<String> mCandidates;
    private ArrayList<Double> mPercentages;
    private ArrayList<Representative> mRepresentatives;

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
        mLocation = 94704;
        mDistrict = "13th Congressional District";
        mCounty = "Alameda County, CA";
        mCandidates = new ArrayList<String>();
        mCandidates.add("Obama");
        mCandidates.add("Romney");
        mPercentages = new ArrayList<Double>();
        mPercentages.add(78.9);
        mPercentages.add(18.2);
        toCongressionalView();
    }

    public void findLocation(View view) {
        // make EditText view
        mLocation = 94704;
        mDistrict = "13th Congressional District";
        mCounty = "Alameda County, CA";
        toCongressionalView();
    }

    public void loadRepresentatives() {
        // use mLocation to get representatives
        mRepresentatives = new ArrayList<Representative>();
        /* ---- HARDCODE ---- */
        // Barbara Boxer
        Representative SenBarbaraBoxer = new Representative("Barbara", "Levy", "Boxer", "Democratic");
        SenBarbaraBoxer.setMyHouse(true);
        SenBarbaraBoxer.setMyPortrait(R.drawable.boxer);
        SenBarbaraBoxer.setMyTerm("Jan. 3, 2010", "Jan. 3, 2016");
        SenBarbaraBoxer.setMySocial("boxer.senate.gov", "senbarbaraboxer@gmail.com", "@SenatorBoxer");
        SenBarbaraBoxer.addCommittee("Committee on Commerce, Science, and Transportation");
        SenBarbaraBoxer.addCommittee("Committee on Environment and Public Works");
        SenBarbaraBoxer.addCommittee("Committee on Foreign Relations");
        SenBarbaraBoxer.addCommittee("Select Committee on Ethics");
        SenBarbaraBoxer.addBill("Feb. 3, 2016", 2487, "Female Veteran Suicide Prevention Act");
        mRepresentatives.add(SenBarbaraBoxer);

        // Dianne Feinstein
        Representative SenDianneFeinstein = new Representative("Dianne", "Goldman", "Feinstein", "Democratic");
        SenDianneFeinstein.setMyHouse(true);
        SenDianneFeinstein.setMyPortrait(R.drawable.feinstein);
        SenDianneFeinstein.setMyTerm("Jan. 3, 2012", "Jan. 3, 2018");
        SenDianneFeinstein.setMySocial("feintsein.senate.gov", "sendiannefeinstein@gmail.com", "@SenFeinstein");
        SenDianneFeinstein.addCommittee("Committee on Appropriations");
        SenDianneFeinstein.addCommittee("Committee on the Judicary");
        SenDianneFeinstein.addCommittee("Committee on Rules and Administration");
        SenDianneFeinstein.addCommittee("Select Committee on Intelligence");
        SenDianneFeinstein.addBill("Feb. 23, 2016", 2568, "California Desert Conservation, Off-Road Recreation, and Renewable Energy Act");
        SenDianneFeinstein.addBill("Feb. 11, 2016", 2552, "Interstate Threats Clarification Act of 2016");
        SenDianneFeinstein.addBill("Feb. 10, 2016", 2533, "California Long-Term Provisions for Water Supply and Short-Term Provisions for Emergency Drought Relief Act");
        SenDianneFeinstein.addBill("Jan. 12, 2016", 2442, "A bill to authorize the use of passenger facility charges at an airport previously associated with the airport at which the charges are collected");
        SenDianneFeinstein.addBill("Jan. 20, 2016", 2422, "Fiscal Year 2016 Department of Veterans Affairs Seismic Safety and Construction Authorization Act");
        mRepresentatives.add(SenDianneFeinstein);

        // Barbara Lee
        Representative RepBarbaraLee = new Representative("Barbara", "Jean", "Lee", "Democratic");
        RepBarbaraLee.setMyHouse(false);
        RepBarbaraLee.setMyPortrait(R.drawable.lee);
        RepBarbaraLee.setMyTerm("Jan. 3, 2015", "Jan. 3, 2017");
        RepBarbaraLee.setMySocial("lee.house.gov", "repbarbaralee@gmail.com", "@RepBarbaraLee");
        RepBarbaraLee.addCommittee("Committee on Appropriations");
        RepBarbaraLee.addCommittee("House Committee on The Budget");
        mRepresentatives.add(RepBarbaraLee);
    }

    public void toCongressionalView() {
        loadRepresentatives();
        Intent intent = new Intent(this, CongressionalActivity.class);
        intent.putExtra("LOCATION", "CA, " + mLocation);
        intent.putExtra("DISTRICT", mDistrict);
        intent.putExtra("REPRESENTATIVES", mRepresentatives);
        startActivity(intent);

        Intent serviceIntent = new Intent(this, PhoneToWatchService.class);
        serviceIntent.putExtra("LOCATION", "CA, " + mLocation);
        serviceIntent.putExtra("DISTRICT", mDistrict);
        serviceIntent.putExtra("REPRESENTATIVES", mRepresentatives);
        serviceIntent.putExtra("COUNTY", mCounty);
        serviceIntent.putExtra("CANDIDATES", mCandidates);
        serviceIntent.putExtra("PERCENTAGES", mPercentages);
        startService(serviceIntent);
    }
}
