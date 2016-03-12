package com.erchpito.represent;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
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
    private Button mWebsiteButton;
    private Button mEmailButton;
    private TextView mCareerText;

    private String mWebsite;
    private String mEmail;

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

        mWebsiteButton = (Button) findViewById(R.id.website_text);
        mWebsiteButton.setTypeface(font);

        mEmailButton = (Button) findViewById(R.id.email_text);
        mEmailButton.setTypeface(font);

        mCareerText = (TextView) findViewById(R.id.career_text);
        mCareerText.setTypeface(font);
        mCareerText.setMovementMethod(new ScrollingMovementMethod());

        Representative rep = mRepresentative;

        mPortraitImage.setImageBitmap(rep.getMyPortraitBit());
        mNameText.setText(((rep.isSenator()) ? "Sen. " : "Rep. ") + rep.getMyName());
        mPartyText.setText(rep.getMyParty());
        mTermText.setText(rep.getMyTerm());
        mWebsiteButton.setText("Website");
        mWebsite = rep.getMyWebsite();
        mEmailButton.setText("Email");
        mEmail = rep.getMyEmail();


        String render = "";
        render += "Committees:\n\n";
        for (int i = 0; i < rep.getNumCommittees(); i++) {
            render += rep.getMyCommittees(i) + "\n";
        }
        render += "\nSponsored Bills:\n\n";
        for (int i = 0; i < rep.getNumBills(); i++) {
            render += rep.getMyBills(i) + "\n\n";
        }
        mCareerText.setText(render);

        mHomeLayout = (RelativeLayout) findViewById(R.id.home);
        int color = ContextCompat.getColor(this,  R.color.oldGloryGrey);
        if (rep.getMyParty().equals("Republican")) {
            color = ContextCompat.getColor(this,  R.color.oldGloryRed);
        } else if (rep.getMyParty().equals("Democratic")) {
            color = ContextCompat.getColor(this,  R.color.oldGloryBlue);
        } else if (rep.getMyParty().equals("Green")) {
            color = ContextCompat.getColor(this,  R.color.oldGloryGreen);
        } else if (rep.getMyParty().equals("Libertarian")) {
            color = ContextCompat.getColor(this,  R.color.oldGloryYellow);
        }
        mHomeLayout.setBackgroundColor(color);
        // via ProgrammingLife.io
        float[] hsv = new float[3];
        int brandColor = Color.parseColor(String.format("#%06X", (0xFFFFFF & color)));
        Color.colorToHSV(brandColor, hsv);
        hsv[1] = hsv[1] + 0.1f;
        hsv[2] = hsv[2] - 0.1f;
        int argbColor = Color.HSVToColor(hsv);
        getWindow().setStatusBarColor(Color.parseColor(String.format("#%08X", argbColor)));
        mWebsiteButton.setBackgroundColor(Color.parseColor(String.format("#%08X", argbColor)));
        mEmailButton.setBackgroundColor(Color.parseColor(String.format("#%08X", argbColor)));
    }

    public void onClick(View view) {
        if (view.getId() == mEmailButton.getId()) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mEmail, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } else if (view.getId() == mWebsiteButton.getId()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(mWebsite));
            startActivity(i);
        }
    }

}
