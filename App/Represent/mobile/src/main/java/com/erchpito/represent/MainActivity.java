package com.erchpito.represent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erchpito.common.Representative;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private int mAnimationDuration;

    private Typeface font;

    private RelativeLayout mButtonField;

    private TextView mRepresentText;
    private EditText mZipEdit;
    private Button mZipButton;
    private Button mCurrentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        font = Typeface.createFromAsset(getAssets(), "fonts/LeagueSpartan-Bold.otf");
        mAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

//        mButtonField = (RelativeLayout) findViewById(R.id.button_field);

        mRepresentText = (TextView) findViewById(R.id.represent_text);
        mRepresentText.setTypeface(font);

//        mZipButton = (Button) findViewById(R.id.zip_button);
//        mZipButton.setTypeface(font);

//        mCurrentButton = (Button) findViewById(R.id.current_button);
        mCurrentButton = (Button) findViewById(R.id.zip_current);
        mCurrentButton.setTypeface(font);

        mZipEdit = (EditText) findViewById(R.id.zip_edit);
        mZipEdit.setTypeface(font);
//        mZipEdit.setVisibility(View.GONE);
        mZipEdit.setFocusableInTouchMode(true);
        mZipEdit.setHintTextColor(ContextCompat.getColor(this, R.color.white));
        mZipEdit.requestFocus();
        mZipEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    toCongressionalView(Integer.parseInt(mZipEdit.getText().toString()));
                    return true;
                }
                return false;
            }
        });
    }

    public void enterLocation(View view) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mZipEdit.setAlpha(0f);
//                mZipEdit.setVisibility(View.VISIBLE);
//
//                mZipEdit.animate()
//                        .alpha(1f)
//                        .setDuration(mAnimationDuration)
//                        .setListener(null);
//
//                mButtonField.animate()
//                        .alpha(0f)
//                        .setDuration(mAnimationDuration)
//                        .setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                mButtonField.setVisibility(View.GONE);
//                            }
//                        });
//            }
//        }, 0);
        toCongressionalView(RepresentCalculator.findZipCode(0, 0));
    }

    public void findLocation(View view) {
        // make EditText view
        int zipcode = 94704;
        toCongressionalView(zipcode);
    }

    public void toCongressionalView(int zipcode) {
        String location = RepresentCalculator.findLocation(zipcode);
        String district = RepresentCalculator.findCongressionalDistrict(zipcode);
        String county = RepresentCalculator.findCounty(zipcode);
        int color = RepresentCalculator.findColor(zipcode, this);
        ArrayList<String> votes = RepresentCalculator.find2012Vote(zipcode);
        ArrayList<Representative> representatives = RepresentCalculator.findRepresentatives(zipcode);

        Intent intent = new Intent(this, CongressionalActivity.class);
        intent.putExtra("LOCATION", location);
        intent.putExtra("DISTRICT", district);
        intent.putExtra("REPRESENTATIVES", representatives);
        intent.putExtra("COLOR", color);
        startActivity(intent);

        Intent serviceIntent = new Intent(this, PhoneToWatchService.class);
        serviceIntent.putExtra("LOCATION", location);
        serviceIntent.putExtra("DISTRICT", district);
        serviceIntent.putExtra("REPRESENTATIVES", representatives);
        serviceIntent.putExtra("COLOR", color);
        serviceIntent.putExtra("COUNTY", county);
        serviceIntent.putExtra("VOTES", votes);
        Log.d(TAG, "starting service");
        startService(serviceIntent);
    }
}
