package com.erchpito.represent;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erchpito.common.Representative;

import java.util.ArrayList;

public class DetailedActivity extends WearableActivity {

    private static final String TAG = "WearDetailedActivity";

    private Representative mRepresentative;
    private Typeface font;

    private ImageView mPortraitImage;
    private TextView mNameText;

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

        Representative rep = mRepresentative;

        mPortraitImage.setImageResource(rep.getMyPortrait());
        mNameText.setText(((rep.isSenator()) ? "Sen.\n" : "Rep.\n") + rep.getMyName() + " - " + rep.getMyParty().substring(0, 1));
    }
}
