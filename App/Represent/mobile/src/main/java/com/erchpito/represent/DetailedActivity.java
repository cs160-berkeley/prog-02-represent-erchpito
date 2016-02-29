package com.erchpito.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailedActivity extends AppCompatActivity {

    private Representative mRepresentative;

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

        Intent intent = getIntent();
        mRepresentative = intent.getParcelableExtra(CongressionalActivity.REPRESENTATIVE);

        mPortraitImage = (ImageView) findViewById(R.id.portrait_image);
        mNameText = (TextView) findViewById(R.id.name_text);
        mPartyText = (TextView) findViewById(R.id.party_text);
        mTermText = (TextView) findViewById(R.id.term_text);
        mWebsiteText = (TextView) findViewById(R.id.website_text);
        mEmailText = (TextView) findViewById(R.id.email_text);
        mCareerText = (TextView) findViewById(R.id.career_text);

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
            render += rep.getMyCommittees(i) + "\n";
        }
        render += "\nSponsored Bills:\n";
        for (int i = 0; i < rep.getNumBills(); i++) {
            render += rep.getMyBills(i) + "\n";
        }
        mCareerText.setText(render);
    }

}
