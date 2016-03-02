package com.erchpito.represent;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erchpito.common.Representative;

public class DetailedActivity extends AppCompatActivity {

    private static final String TAG = "DetailedActivity";

    private Representative mRepresentative;
    private Typeface font;

    private RelativeLayout mHomeLayout;

    private ImageView mPortraitImage;
    private TextView mNameText;
    private TextView mPartyText;
    private TextView mTermText;
    private TextView mWebsiteText;
    private TextView mEmailText;
    private TextView mCareerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        font = Typeface.createFromAsset(getAssets(), "fonts/LeagueSpartan-Bold.otf");

        Bundle bundle = getIntent().getExtras();
        mRepresentative = bundle.getParcelable("REPRESENTATIVE");

        mPortraitImage = (ImageView) findViewById(R.id.portrait_image);
        mNameText = (TextView) findViewById(R.id.name_text);
        mNameText.setTypeface(font);

        mPartyText = (TextView) findViewById(R.id.party_text);
        mPartyText.setTypeface(font);

        mTermText = (TextView) findViewById(R.id.term_text);
        mTermText.setTypeface(font);

        mWebsiteText = (TextView) findViewById(R.id.website_text);
        mWebsiteText.setTypeface(font);

        mEmailText = (TextView) findViewById(R.id.email_text);
        mEmailText.setTypeface(font);

        mCareerText = (TextView) findViewById(R.id.career_text);
        mCareerText.setTypeface(font);
        mCareerText.setMovementMethod(new ScrollingMovementMethod());

        Representative rep = mRepresentative;

        mPortraitImage.setImageResource(rep.getMyPortrait());
        mNameText.setText(((rep.isSenator()) ? "Sen. " : "Rep. ") + rep.getMyName());
        mPartyText.setText(rep.getMyParty());
        mTermText.setText(rep.getMyTerm());
        mWebsiteText.setText(rep.getMyWebsite());
        mEmailText.setText(rep.getMyEmail());

        String render = "";
        render += "Committees:\n";
        for (int i = 0; i < rep.getNumCommittees(); i++) {
            render += "\t" + rep.getMyCommittees(i) + "\n";
        }
        render += "\nSponsored Bills:\n";
        for (int i = 0; i < rep.getNumBills(); i++) {
            render += "\t" + rep.getMyBills(i) + "\n\n";
        }
        mCareerText.setText(render);

        mHomeLayout = (RelativeLayout) findViewById(R.id.home);
        int color = ContextCompat.getColor(this, R.color.grey);
        if (rep.getMyParty().equals("Republican")) {
            color = ContextCompat.getColor(this, R.color.oldGloryRed);
        } else if (rep.getMyParty().equals("Democratic")) {
            color = ContextCompat.getColor(this, R.color.oldGloryBlue);
        }
        mHomeLayout.setBackgroundColor(color);
    }

}
