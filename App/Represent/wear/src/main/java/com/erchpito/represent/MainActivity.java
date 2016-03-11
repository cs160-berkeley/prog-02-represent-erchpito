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
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erchpito.common.Representative;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends WearableActivity implements WearableListView.ClickListener, SensorEventListener {

    private static final String TAG = "WearMainActivity";

    private int mAnimationDuration;
    private Typeface font;
    private String mLocation;
    private String mDistrict;
    private int mColor;
    private double accelersum;
    private ArrayList<Representative> mRepresentatives;
    private ArrayList<String> mVotes;

    private RepresentativeAdapter mRepresentativeAdapter;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private WearableListView mRepresentativeList;
    private RelativeLayout mLocationField;
    private RelativeLayout mHomeLayout;
    private TextView mLocationText;
    private TextView mDistrictText;
    private TextView mVoteText;

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
            mColor = bundle.getInt("COLOR");
            mRepresentatives = bundle.getParcelableArrayList("REPRESENTATIVES");
            mVotes = bundle.getStringArrayList("VOTES");
        } else {
            finish();
            Log.d(TAG, "finishing Activity");
            mVotes = new ArrayList<String>();
            mRepresentatives = new ArrayList<Representative>();
        }

        mAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

        accelersum = -1.0;

        mLocationText = (TextView) findViewById(R.id.location_text);
        mLocationText.setText(mLocation);
        mLocationText.setTypeface(font);

        mDistrictText = (TextView) findViewById(R.id.district_text);
        mDistrictText.setText(mDistrict);
        mDistrictText.setTypeface(font);

        mVoteText = (TextView) findViewById(R.id.vote_text);
        String render = "2012 Presidential Vote\n" + mVotes.get(0) + "\n\n";
        for (int i = 1; i < mVotes.size(); i++) {
            render += mVotes.get(i) + "\n\n";
        }
        mVoteText.setText(render);
        mVoteText.setTypeface(font);
        mVoteText.setVisibility(View.GONE);

        mLocationField = (RelativeLayout) findViewById(R.id.field);
        mLocationField.setVisibility(View.VISIBLE);

        mHomeLayout = (RelativeLayout) findViewById(R.id.home);
        mHomeLayout.setBackgroundColor(mColor);

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
                        .setDuration(mAnimationDuration)
                        .setListener(null);

                mLocationField.animate()
                        .alpha(0f)
                        .setDuration(mAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mLocationField.setVisibility(View.GONE);
                            }
                        });
            }
        }, 4500);
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
            serviceIntent.putExtra("ACTION", "random");
            startService(serviceIntent);

            mLocationField.setAlpha(0f);
            mLocationField.setVisibility(View.VISIBLE);

            mLocationField.animate()
                    .alpha(1f)
                    .setDuration(mAnimationDuration)
                    .setListener(null);

            mRepresentativeList.animate()
                    .alpha(0f)
                    .setDuration(mAnimationDuration)
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
        intent.putExtra("ACTION", "detailed");
        intent.putExtra("INDEX", tag);
        intent.putExtra("ZIP", mLocation.substring(mLocation.length() - 5));
        Log.d(TAG, "Starting Detailed View");
        startActivity(intent);
    }

    @Override
    public void onTopEmptyRegionClick() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mVoteText.setAlpha(0f);
                mVoteText.setVisibility(View.VISIBLE);

                mVoteText.animate()
                        .alpha(1f)
                        .setDuration(mAnimationDuration)
                        .setListener(null);

                mRepresentativeList.animate()
                        .alpha(0f)
                        .setDuration(mAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mRepresentativeList.setVisibility(View.GONE);
                            }
                        });
            }
        }, 0);
    }

    public void disappear(View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRepresentativeList.setAlpha(0f);
                mRepresentativeList.setVisibility(View.VISIBLE);

                mRepresentativeList.animate()
                        .alpha(1f)
                        .setDuration(mAnimationDuration)
                        .setListener(null);

                mVoteText.animate()
                        .alpha(0f)
                        .setDuration(mAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mVoteText.setVisibility(View.GONE);
                            }
                        });
            }
        }, 0);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { ; }
}


