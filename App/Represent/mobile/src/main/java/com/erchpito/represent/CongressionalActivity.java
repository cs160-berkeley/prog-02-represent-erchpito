package com.erchpito.represent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erchpito.common.Representative;

import java.util.ArrayList;

public class CongressionalActivity extends AppCompatActivity {

    private static final String TAG = "CongressionalActivity";

    private Typeface font;
    private String mLocation;
    private String mDistrict;
    private int mColor;
    private ArrayList<Representative> mRepresentatives;
    private RepresentativeArrayAdapter mRepresentativeAdapter;

    private RelativeLayout mHomeLayout;

    private ListView mRepresentativeList;
    private TextView mLocationText;
    private TextView mDistrictText;

    private class RepresentativeArrayAdapter extends ArrayAdapter<Representative> {

        private Context mContext;

        public RepresentativeArrayAdapter(Context context) {
            super(context, R.layout.representative_list_view, mRepresentatives);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.representative_list_view, parent, false);
            }

            RelativeLayout home = (RelativeLayout) convertView.findViewById(R.id.list_view_home);

            TextView name = (TextView) convertView.findViewById(R.id.list_view_name);
            name.setTypeface(font);

            TextView website = (TextView) convertView.findViewById(R.id.list_view_website);
            website.setTypeface(font);

            TextView email = (TextView) convertView.findViewById(R.id.list_view_email);
            email.setTypeface(font);

            TextView tweet = (TextView) convertView.findViewById(R.id.list_view_tweet);
            tweet.setTypeface(font);

            ImageView portrait = (ImageView) convertView.findViewById(R.id.list_view_portrait);
            ImageButton button = (ImageButton) convertView.findViewById(R.id.list_view_button);

            final Representative rep = mRepresentatives.get(position);

            name.setText(((rep.isSenator()) ? "Sen. " : "Rep. ") +
                         rep.getMyName() + " - " +
                         rep.getMyParty().substring(0, 1));
            website.setText(rep.getMyWebsite());
            email.setText(rep.getMyEmail());
            tweet.setText(rep.getMyLastTweet());
//            // TODO: Use a more specific parent
//            final ViewGroup parentView = (ViewGroup) getWindow().getDecorView().getRootView();
//            // TODO: Base this Tweet ID on some data from elsewhere in your app
//            long tweetId = 631879971628183552L;
//            TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
//                @Override
//                public void success(Result<Tweet> result) {
//                    TweetView tweetView = new TweetView(MainActivity.this, result.data);
//                    parentView.addView(tweetView);
//                }
//                @Override
//                public void failure(TwitterException exception) {
//                    Log.d("TwitterKit", "Load Tweet failure", exception);
//                }
//            });

            portrait.setImageResource(rep.getMyPortrait());

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DetailedActivity.class);
                    intent.putExtra("REPRESENTATIVE", rep);
                    Log.d(TAG, "Starting Detailed View");
                    startActivity(intent);
                }
            });

            int color = ContextCompat.getColor(mContext, R.color.oldGloryGrey);
            if (rep.getMyParty().equals("Republican")) {
                color = ContextCompat.getColor(mContext,  R.color.oldGloryRed);
            } else if (rep.getMyParty().equals("Democratic")) {
                color = ContextCompat.getColor(mContext,  R.color.oldGloryBlue);
            } else if (rep.getMyParty().equals("Green")) {
                color = ContextCompat.getColor(mContext,  R.color.oldGloryGreen);
            } else if (rep.getMyParty().equals("Libertarian")) {
                color = ContextCompat.getColor(mContext,  R.color.oldGloryYellow);
            }
            home.setBackgroundColor(color);
            // via ProgrammingLife.io
            float[] hsv = new float[3];
            int brandColor = Color.parseColor(String.format("#%06X", (0xFFFFFF & color)));
            Color.colorToHSV(brandColor, hsv);
            hsv[1] = hsv[1] + 0.1f;
            hsv[2] = hsv[2] - 0.1f;
            int argbColor = Color.HSVToColor(hsv);
            getWindow().setStatusBarColor(Color.parseColor(String.format("#%08X", argbColor)));
            mHomeLayout.setElevation((float) 8);
            button.setBackgroundColor(Color.parseColor(String.format("#%08X", argbColor)));

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional);

        font = Typeface.createFromAsset(getAssets(), "fonts/LeagueSpartan-Bold.otf");

        Bundle bundle = getIntent().getExtras();
        mLocation = bundle.getString("LOCATION");
        mDistrict = bundle.getString("DISTRICT");
        mRepresentatives = bundle.getParcelableArrayList("REPRESENTATIVES");
        mColor = bundle.getInt("COLOR");

        mLocationText = (TextView) findViewById(R.id.location_text);
        mLocationText.setText(mLocation);
        mLocationText.setTypeface(font);

        mDistrictText = (TextView) findViewById(R.id.district_text);
        mDistrictText.setText(mDistrict);
        mDistrictText.setTypeface(font);

        mRepresentativeList = (ListView) findViewById(R.id.representative_list);
        mRepresentativeAdapter = new RepresentativeArrayAdapter(this);
        mRepresentativeList.setAdapter(mRepresentativeAdapter);

        mHomeLayout = (RelativeLayout) findViewById(R.id.home);
        mHomeLayout.setBackgroundColor(mColor);
        // via ProgrammingLife.io
        float[] hsv = new float[3];
        int brandColor = Color.parseColor(String.format("#%06X", (0xFFFFFF & mColor)));
        Color.colorToHSV(brandColor, hsv);
        hsv[1] = hsv[1] + 0.1f;
        hsv[2] = hsv[2] - 0.1f;
        int argbColor = Color.HSVToColor(hsv);
        getWindow().setStatusBarColor(Color.parseColor(String.format("#%08X", argbColor)));
        mHomeLayout.setElevation((float) 8);
    }

}
