package com.erchpito.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.wearable.DataMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by erchpito on 28/2/2016.
 */
public class Representative implements Parcelable {

    // Name
    private String mFirstName;
    private String mLastName;

    // Picture
    private int mPortrait;

    // Social
    private String mWebsite;
    private String mEmail;
    private String mTwitter;
    private String mLastTweet;

    // Career
    private boolean mIsSenator;
    private String mStartTerm;
    private String mEndTerm;
    private String mParty;
    private ArrayList<String> mCommittees;
    private ArrayList<Bill> mBills;

    public static final Parcelable.Creator<Representative> CREATOR
            = new Parcelable.Creator<Representative>() {

        @Override
        public Representative createFromParcel(Parcel in) {
            return new Representative(in);
        }

        @Override
        public Representative[] newArray(int size) {
            return new Representative[size];
        }

    };

    public Representative() {
        mCommittees = new ArrayList<String>();
        mBills = new ArrayList<Bill>();
    }

    public Representative(String firstName, String lastName, String party) {
        this();
        mFirstName = firstName;
        mLastName = lastName;
        if (party.equals("D")) {
            mParty = "Democratic";
        } else if (party.equals("R")) {
            mParty = "Republican";
        } else if (party.equals("I")) {
            mParty = "Independent";
        } else if (party.equals("G")) {
            mParty = "Green";
        } else if (party.equals("L")) {
            mParty = "Libertarian";
        } else {
            mParty = party;
        }
    }

    private Representative(Parcel in) {
        this();
        mFirstName = in.readString();
        mLastName = in.readString();
        mPortrait = in.readInt();
        mWebsite = in.readString();
        mEmail = in.readString();
        mTwitter = in.readString();
        mLastTweet = in.readString();
        mIsSenator = in.readByte() != 0;
        mStartTerm = in.readString();
        mEndTerm = in.readString();
        mParty = in.readString();
        in.readStringList(mCommittees);
        in.readTypedList(mBills, Bill.CREATOR);
    }

    public Representative(DataMap in) {
        this(in.getString("firstName"), in.getString("lastName"), in.getString("party"));
        mIsSenator = in.getByte("isSenator") != 0;
        mPortrait = in.getInt("portrait");
        mWebsite = "";
        mEmail = "";
        mTwitter = "";
        mStartTerm = "";
        mEndTerm = "";
    }

    public void writeToDataMap(DataMap out) {
        out.putString("firstName", mFirstName);
        out.putString("lastName", mLastName);
        out.putInt("portrait", mPortrait);
        out.putByte("isSenator", (byte) (mIsSenator ? 1 : 0));
        out.putString("party", mParty);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mFirstName);
        out.writeString(mLastName);
        out.writeInt(mPortrait);
        out.writeString(mWebsite);
        out.writeString(mEmail);
        out.writeString(mTwitter);
        out.writeString(mLastTweet);
        out.writeByte((byte) (mIsSenator ? 1 : 0));
        out.writeString(mStartTerm);
        out.writeString(mEndTerm);
        out.writeString(mParty);
        out.writeStringList(mCommittees);
        out.writeTypedList(mBills);
    }

    @Override
    public int describeContents() { return 0; }

    public void addBill(String date, int number, String title) {
        mBills.add(new Bill(mIsSenator, date, number, title));
    }

    public void addCommittee(String committee) { mCommittees.add(committee); }

    public String getMyBills(int i) { return mBills.get(i).toString(); }

    public String getMyCommittees(int i) { return mCommittees.get(i); }

    public String getMyEmail() { return mEmail; }

    public boolean getMyHouse() { return isSenator(); }

    public String getMyLastTweet() { return mLastTweet; }

    public String getMyName() { return mFirstName + " " + mLastName; }

    public String getMyParty() { return mParty; }

    public int getMyPortrait() { return mPortrait; }

    public String getMyTerm() { return "Term: " + mStartTerm + " - " + mEndTerm; }

    public String getMyWebsite() { return mWebsite; }

    public int getNumBills() { return mBills.size(); }

    public int getNumCommittees() { return mCommittees.size(); }

    public boolean isSenator() { return mIsSenator; }

    public void setMyHouse(boolean isSenator) {
        mIsSenator = isSenator;
    }

    public void setMyLastTweet(String tweet) { mLastTweet = tweet; }

    public void setMyPortrait(int img) { mPortrait = img; }

    public void setMySocial(String website, String email, String twitterHandle) {
        mWebsite = website;
        mEmail = email;
        mTwitter = twitterHandle;
    }

    public void setMyTerm(String start, String end) {
        try {
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-mm-dd");
            SimpleDateFormat newFormat = new SimpleDateFormat("MMM. dd, yyyy");
            mStartTerm = newFormat.format(oldFormat.parse(start));
            mEndTerm = newFormat.format(oldFormat.parse(end));
        } catch (Exception e) {
            mStartTerm = "";
            mEndTerm = "";
        }
    }
}

class Bill implements Parcelable {

    private String mDate;
    private int mNumber;
    private String mTitle;
    private boolean mInSenate;

    public Bill(boolean inSenate, String date, int number, String title) {
        mInSenate = inSenate;
        try {
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-mm-dd");
            SimpleDateFormat newFormat = new SimpleDateFormat("MMM. dd, yyyy");
            mDate = newFormat.format(oldFormat.parse(date));
        } catch (Exception e) {
            mDate = "";
        }
        mNumber = number;
        mTitle = title;
    }

    public Bill(Parcel in) {
        mInSenate = in.readByte() != 0;
        mDate = in.readString();
        mNumber = in.readInt();
        mTitle = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeByte((byte) (mInSenate ? 1 : 0));
        out.writeString(mDate);
        out.writeInt(mNumber);
        out.writeString(mTitle);
    }

    public static final Parcelable.Creator<Bill> CREATOR = new Parcelable.Creator<Bill>() {
        @Override
        public Bill createFromParcel(Parcel in) {
            return new Bill(in);
        }

        @Override
        public Bill[] newArray(int size) {
            return new Bill[size];
        }
    };

    public String toString() {
        String render = "";
        render += (mInSenate ? "S." : "H.R.") + " " + mNumber + " | ";
        render += mDate + "\n";
        render += mTitle;
        return render;
    }
}
