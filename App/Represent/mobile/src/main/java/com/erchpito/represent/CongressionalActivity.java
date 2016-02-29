package com.erchpito.represent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class CongressionalActivity extends AppCompatActivity {

    private static final String TAG = "CongressionalActivity";
    public final static String REPRESENTATIVE = "com.erchpito.represent.REPRESENTATIVE";

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
            TextView website = (TextView) convertView.findViewById(R.id.list_view_website);
            TextView email = (TextView) convertView.findViewById(R.id.list_view_email);
            TextView tweet = (TextView) convertView.findViewById(R.id.list_view_tweet);
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
                    intent.putExtra(REPRESENTATIVE, rep);
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

        Bundle bundle = getIntent().getExtras();
        mLocation = bundle.getString(MainActivity.LOCATION);
        mDistrict = bundle.getString(MainActivity.DISTRICT);
        mRepresentatives = bundle.getParcelableArrayList(MainActivity.REPRESENTATIVES);

        mLocationText = (TextView) findViewById(R.id.location_text);
        mLocationText.setText(mLocation);

        mDistrictText = (TextView) findViewById(R.id.district_text);
        mDistrictText.setText(mDistrict);

        mRepresentativeList = (ListView) findViewById(R.id.representative_list);
        mRepresentativeAdapter = new RepresentativeArrayAdapter(this);
        mRepresentativeList.setAdapter(mRepresentativeAdapter);
    }

}
