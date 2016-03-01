package com.erchpito.represent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.erchpito.common.Representative;

import java.util.ArrayList;

public class CongressionalActivity extends AppCompatActivity {

    private static final String TAG = "CongressionalActivity";

    private Typeface font;
    private String mLocation;
    private String mDistrict;
    private ArrayList<Representative> mRepresentatives;
    private RepresentativeArrayAdapter mRepresentativeAdapter;

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

        mLocationText = (TextView) findViewById(R.id.location_text);
        mLocationText.setText(mLocation);
        mLocationText.setTypeface(font);

        mDistrictText = (TextView) findViewById(R.id.district_text);
        mDistrictText.setText(mDistrict);
        mDistrictText.setTypeface(font);

        mRepresentativeList = (ListView) findViewById(R.id.representative_list);
        mRepresentativeAdapter = new RepresentativeArrayAdapter(this);
        mRepresentativeList.setAdapter(mRepresentativeAdapter);
    }

}
