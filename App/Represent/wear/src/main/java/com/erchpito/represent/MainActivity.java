package com.erchpito.represent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erchpito.common.Representative;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity implements WearableListView.ClickListener, SensorEventListener {

    private static final String TAG = "WearMainActivity";

    private int mShortAnimationDuration;
    private Typeface font;
    private String mLocation;
    private String mDistrict;
    private double accelersum;
    private ArrayList<Representative> mRepresentatives;

    private RepresentativeAdapter mRepresentativeAdapter;
    private GestureDetector mDetector;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private WearableListView mRepresentativeList;
    private RelativeLayout mLocationField;
    private TextView mLocationText;
    private TextView mDistrictText;

    private class RepresentativeAdapter extends WearableListView.Adapter {

        private Context mContext;
        private LayoutInflater mInflater;

        public RepresentativeAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        // Provide a reference to the type of views you're using
        public class ItemViewHolder extends WearableListView.ViewHolder {
            private TextView mName;
            public ItemViewHolder(View itemView) {
                super(itemView);
                // find the text view within the custom item's layout
                mName = (TextView) itemView.findViewById(R.id.list_view_name);
            }
        }

        // Create new views for list items
        // (invoked by the WearableListView's layout manager)
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
            // Inflate our custom layout for list items
            return new ItemViewHolder(mInflater.inflate(R.layout.representative_list_view, null));
        }

        // Replace the contents of a list item
        // Instead of creating new views, the list tries to recycle existing ones
        // (invoked by the WearableListView's layout manager)
        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            // retrieve the text view
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            TextView name = itemHolder.mName;
            // replace text contents
            Representative rep = mRepresentatives.get(position);
            name.setText(((rep.isSenator()) ? "Sen. " : "Rep. ") + rep.getMyName());
            // replace list item's metadata
            holder.itemView.setTag(position);
        }

        // Return the size of your dataset
        // (invoked by the WearableListView's layout manager)
        @Override
        public int getItemCount() {
            return mRepresentatives.size();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        font = Typeface.createFromAsset(getAssets(), "fonts/LeagueSpartan-Bold.otf");

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mLocation = bundle.getString("LOCATION");
            mDistrict = bundle.getString("DISTRICT");
            Log.d(TAG, mDistrict);
            mRepresentatives = bundle.getParcelableArrayList("REPRESENTATIVES");
        } else {
            mLocation = "CA, 94704";
            mDistrict = "13th Congressional District";
            loadRepresentatives();
        }

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

        accelersum = -1.0;

        mLocationText = (TextView) findViewById(R.id.location_text);
        mLocationText.setText(mLocation);
        mLocationText.setTypeface(font);

        mDistrictText = (TextView) findViewById(R.id.district_text);
        mDistrictText.setText(mDistrict);
        mDistrictText.setTypeface(font);

        mLocationField = (RelativeLayout) findViewById(R.id.field);
        mLocationField.setVisibility(View.VISIBLE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mRepresentativeList = (WearableListView) findViewById(R.id.representative_list);
        mRepresentativeAdapter = new RepresentativeAdapter(this);
        mRepresentativeList.setAdapter(mRepresentativeAdapter);
        mRepresentativeList.setClickListener(this);
        mRepresentativeList.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRepresentativeList.setAlpha(0f);
                mRepresentativeList.setVisibility(View.VISIBLE);

                mRepresentativeList.animate()
                        .alpha(1f)
                        .setDuration(mShortAnimationDuration)
                        .setListener(null);

                mLocationField.animate()
                        .alpha(0f)
                        .setDuration(mShortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mLocationField.setVisibility(View.GONE);
                            }
                        });
            }
        }, 4500);

        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent ev) {
                Log.d(TAG, "POTATO");
            }
        });

//        Intent intent = new Intent(this, VoteActivity.class);
//        intent.putExtra("COUNTY", bundle.getString("COUNTY"));
//        intent.putExtra("CANDIDATES", bundle.getParcelableArrayList("CANDIDATES"));
//        intent.putExtra("PERCENTAGES", bundle.getParcelableArrayList("PERCENTAGES"));
//        startService(intent);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            Log.d(TAG, bundle.getString("DISTRICT"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        double localAccelersum = 0;
        for (float f : event.values) {
            localAccelersum += f;
        }

        if (accelersum == -1) {
            accelersum = localAccelersum;
        }

        if (accelersum != localAccelersum && !mLocationText.getText().equals("SHAKE")) {
            accelersum = localAccelersum;
            mLocationText.setText("SHAKE");
            mDistrictText.setText("");

            Intent serviceIntent = new Intent(this, WatchToPhoneService.class);
            startService(serviceIntent);

            mLocationField.setAlpha(0f);
            mLocationField.setVisibility(View.VISIBLE);

            mLocationField.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);

            mRepresentativeList.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mRepresentativeList.setVisibility(View.GONE);
                        }
                    });
        }
    }

    // WearableListView click listener
    @Override
    public void onClick(WearableListView.ViewHolder v) {
        Integer tag = (Integer) v.itemView.getTag();
        Intent intent = new Intent(this, DetailedActivity.class);
        intent.putExtra("REPRESENTATIVE", mRepresentatives.get(tag));
        Log.d(TAG, "Starting Detailed View");
        startActivity(intent);
    }

    @Override
    public void onTopEmptyRegionClick() { ; }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { ; }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
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
}


