package com.erchpito.represent;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.erchpito.common.Representative;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by erchpito on 2/3/2016.
 */
public final class RepresentCalculator {

    private static final String TAG = "RepresentCalculator";

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "yj6GsqrlTzwAVqEEahPSYFtZs";
    private static final String TWITTER_SECRET = "KJkdLmgO3zc6w8A0kRCcpZV4YIXTY9Xe2BKS6h2zuEEMLv12aW";

    private static HashMap<String, ArrayList<Representative>> repHash = new HashMap<String, ArrayList<Representative>>();
    private static HashMap<String, String> districtHash = new HashMap<String, String>();
    private static HashMap<String, ArrayList<String>> voteHash = new HashMap<String, ArrayList<String>>();
    private static HashMap<String, String> latlngHash = new HashMap<String, String>();
    private static HashMap<String, Boolean> verifyHash = new HashMap<String, Boolean>();

    public static int findColor(String zipcode, Context context) {
        ArrayList<Representative> reps = repHash.get(zipcode);
        for (Representative rep : reps) {
            if (!rep.getMyHouse()) {
                if (rep.getMyParty().equals("Democratic")) {
                    return ContextCompat.getColor(context, R.color.oldGloryBlue);
                } else if (rep.getMyParty().equals("Republican")) {
                    return ContextCompat.getColor(context, R.color.oldGloryRed);
                } else if (rep.getMyParty().equals("Green")) {
                    return ContextCompat.getColor(context, R.color.oldGloryGreen);
                } else if (rep.getMyParty().equals("Libertarian")) {
                    return ContextCompat.getColor(context, R.color.oldGloryYellow);
                } else {
                    return ContextCompat.getColor(context, R.color.oldGloryGrey);
                }
            }
        }
        return ContextCompat.getColor(context, R.color.oldGloryGrey);
    }

    public static String findDistrict(String zipcode) {
        if (!districtHash.containsKey(zipcode)) {
            try {
                String output = "";
                JSONArray result = new APICallTask().execute("https://congress.api.sunlightfoundation.com/districts/locate?zip=" + zipcode).get().getJSONArray("results");
                output += result.getJSONObject(0).getString("state") + ", " + zipcode + "\n";
                int num = result.getJSONObject(0).getInt("district");
                if (num % 10 == 1 && num != 11) {
                    output += num + "st Congressional District";
                } else if (num % 10 == 2 && num != 12) {
                    output += num + "nd Congressional District";
                } else if (num % 10 == 3 && num != 13) {
                    output += num + "rd Congressional District";
                } else {
                    output += num + "th Congressional District";
                }
                districtHash.put(zipcode, output);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "findDistrict(" + zipcode + ") = " + districtHash.get(zipcode));
        return districtHash.get(zipcode);
    }

    public static String findRandomZipCode() {
        return findZipCode(0.0, 0.0);
    }

    public static ArrayList<Representative> findRepresentatives(String zipcode, Context context) {
        if (!repHash.containsKey(zipcode)) {
            try {
                TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
                Fabric.with(context, new Twitter(authConfig));

                ArrayList<Representative> output = new ArrayList<Representative>();
                JSONArray result = new APICallTask().execute("https://congress.api.sunlightfoundation.com/legislators/locate?" + "zip=" + zipcode).get().getJSONArray("results");
                for (int i = 0; i < result.length(); i++) {
                    JSONObject legislator = result.getJSONObject(i);
                    Representative rep = new Representative(legislator.getString("first_name"), legislator.getString("last_name"), legislator.getString("party")); // party is just D or R
                    rep.setMyHouse(legislator.getString("chamber").equals("senate"));
                    rep.setMyTerm(legislator.getString("term_start"), legislator.getString("term_end"));
                    rep.setMySocial(legislator.getString("website"), legislator.getString("oc_email"), legislator.getString("twitter_id")); // twitter_id sans @
                    final String handle = legislator.getString("twitter_id");

                    JSONArray subResult = new APICallTask().execute("https://congress.api.sunlightfoundation.com/committees?" + "member_ids=" + legislator.getString("bioguide_id")).get().getJSONArray("results");
                    Log.d(TAG, subResult.toString());
                    for (int j = 0; j < subResult.length(); j++) {
                        rep.addCommittee(subResult.getJSONObject(j).getString("name"));
                        Log.d(TAG, "Committee: " + subResult.getJSONObject(j).getString("name"));
                    }

                    subResult = new APICallTask().execute("https://congress.api.sunlightfoundation.com/bills?" + "sponsor_id=" + legislator.getString("bioguide_id")).get().getJSONArray("results");
                    Log.d(TAG, subResult.toString());
                    for (int j = 0; j < subResult.length(); j++) {
                        JSONObject bill = subResult.getJSONObject(j);
                        if (!bill.getString("short_title").equals("null")) {
                            rep.addBill(bill.getString("introduced_on"), bill.getInt("number"), bill.getString("short_title"));
                            Log.d(TAG, "Bill: " + bill.getString("short_title"));
                        }
                    }

                    rep.setMyPortrait(R.drawable.boxer);
                    rep.setMyLastTweet("meow");

                    TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                        @Override
                        public void success(Result<AppSession> appSessionResult) {
                            AppSession session = appSessionResult.data;
                            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                            twitterApiClient.getStatusesService().userTimeline(null, handle, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                                @Override
                                public void success(Result<List<Tweet>> listResult) {
                                    for (Tweet tweet : listResult.data) {
//                                        rep.setMyLastTweet(tweet.text);
                                        Log.d("fabricstuff", "result: " + tweet.text + "  " + tweet.createdAt);
                                    }
                                }

                                @Override
                                public void failure(TwitterException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        @Override
                        public void failure(TwitterException e) {
                            e.printStackTrace();
                        }
                    });

                    output.add(rep);
                }
                repHash.put(zipcode, output);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "findRepresentatives(" + zipcode + ") = " + repHash.get(zipcode));
        return repHash.get(zipcode);
    }

    public static ArrayList<String> findVote(String zipcode, int year, Context context) {
        if (!voteHash.containsKey(zipcode)) {
            try {
                String countyName = "";
                JSONArray result = new APICallTask().execute("https://maps.googleapis.com/maps/api/geocode/json?address=" + zipcode + "&result_type=administrative_area_level_2").get().getJSONArray("results");
                JSONArray subresult = result.getJSONObject(0).getJSONArray("address_components");
                for (int i = 0; i < subresult.length(); i++) {
                    JSONObject subobject = subresult.getJSONObject(i);
                    if (subobject.getJSONArray("types").getString(0).equals("administrative_area_level_2")) {
                        countyName = subobject.getString("short_name");
                    }
                }

                ArrayList<String> output = new ArrayList<String>();
                InputStream inputStream = context.getAssets().open("election-county-2012.json");
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();
                JSONArray read = new JSONArray(new String(buffer, "UTF-8"));
                for (int i = 0; i < read.length(); i++) {
                    JSONObject countyResult = read.getJSONObject(i);
                    if (countyName.contains(countyResult.getString("county-name"))) {
                        output.add(countyResult.getString("county-name") + " County, " + countyResult.getString("state-postal"));
                        Double obama = countyResult.getDouble("obama-percentage"); // might not always have the same format of XX.X
                        Double romney = countyResult.getDouble("romney-percentage"); // might not always have the same format of XX.X
                        if (obama >= romney) {
                            output.add("Obama: " + obama + "%");
                            output.add("Romney: " + romney + "%");
                        } else {
                            output.add("Romney: " + romney + "%");
                            output.add("Obama: " + obama + "%");
                        }
                        break;
                    }
                }
                voteHash.put(zipcode, output);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "findVote(" + zipcode + ", " + year + ") = " + voteHash.get(zipcode));
        return voteHash.get(zipcode);
    }

    public static String findZipCode(double lat, double lon) {
        String latlng = "" + lat + "," + lon;
        if (!latlngHash.containsKey(latlng)) {
            try {
                String zipcode = "00000";
                JSONArray result = new APICallTask().execute("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latlng + "&result_type=postal_code").get().getJSONArray("results");
                JSONArray subresult = result.getJSONObject(0).getJSONArray("address_components");
                for (int i = 0; i < subresult.length(); i++) {
                    JSONObject subobject = subresult.getJSONObject(i);
                    if (subobject.getJSONArray("types").getString(0).equals("postal_code")) {
                        zipcode = subobject.getString("short_name");
                    }
                }
                latlngHash.put(latlng, zipcode);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "findZipCode(" + lat + ", " + lon + ") = " + latlngHash.get(latlng));
        return latlngHash.get(latlng);
    }

    public static boolean verifyZipCode(String zipcode) {
        if (!verifyHash.containsKey(zipcode)) {
            try {
                boolean output = false;
                JSONArray result = new APICallTask().execute("https://maps.googleapis.com/maps/api/geocode/json?address=" + zipcode + "&result_type=administrative_area_level_2").get().getJSONArray("results");
                // this is more complicated because other countries can have a zipcode that's note valid in the US.
//                JSONArray subresult = result.getJSONObject(0).getJSONArray("address_components");
//                for (int i = 0; i < subresult.length(); i++) {
//                    JSONObject subobject = subresult.getJSONObject(i);
//                    if (subobject.getJSONArray("types").getString(0).equals("administrative_area_level_2")) {
//                        countyName = subobject.getString("short_name");
//                    }
//                }
                if (result.length() > 0) {
                    output = true;
                }

                verifyHash.put(zipcode, output);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "VerifyZipCode(" + zipcode + ") = " + verifyHash.get(zipcode));
        return verifyHash.get(zipcode);

    }
}
