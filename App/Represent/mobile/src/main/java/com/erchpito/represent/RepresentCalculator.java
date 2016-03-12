package com.erchpito.represent;

import android.content.Context;
import android.graphics.Bitmap;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.fabric.sdk.android.Fabric;

/**
 * Created by erchpito on 2/3/2016.
 */
public final class RepresentCalculator {

    private static final String TAG = "RepresentCalculator";

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "yj6GsqrlTzwAVqEEahPSYFtZs";
    private static final String TWITTER_SECRET = "KJkdLmgO3zc6w8A0kRCcpZV4YIXTY9Xe2BKS6h2zuEEMLv12aW";

    private static Object object;

    private static ArrayList<String> zipcodes = new ArrayList<String>();

    private static HashMap<String, ArrayList<Representative>> repHash = new HashMap<String, ArrayList<Representative>>();
    private static HashMap<String, String> districtHash = new HashMap<String, String>();
    private static HashMap<String, ArrayList<String>> voteHash = new HashMap<String, ArrayList<String>>();
    private static HashMap<String, String> latlngHash = new HashMap<String, String>();
    private static HashMap<String, String> zipHash = new HashMap<String, String>();
    private static HashMap<String, Boolean> verifyHash = new HashMap<String, Boolean>();

    public static int findColor(String latlng, Context context) {
        ArrayList<Representative> reps = repHash.get(latlng);
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

    public static String findDistrict(String latlng) {
        if (!districtHash.containsKey(latlng)) {
            try {
                String [] coord = latlng.split(",");
                String output = "";
                JSONArray result = new APICallTask().execute("https://congress.api.sunlightfoundation.com/districts/locate?latitude=" + coord[0] + "&longitude=" + coord[1]).get().getJSONArray("results");
                output += result.getJSONObject(0).getString("state") + ", " + zipHash.get(latlng) + "\n";
                int num = result.getJSONObject(0).getInt("district");
                if (num % 10 == 1 && num != 11) {
                    output += num + "st Congressional District";
                } else if (num % 10 == 2 && num != 12) {
                    output += num + "nd Congressional District";
                } else if (num % 10 == 3 && num != 13) {
                    output += num + "rd Congressional District";
                } else if (num == 0) {
                    output += " ";
                } else {
                    output += num + "th Congressional District";
                }
                districtHash.put(latlng, output);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "findDistrict(" + latlng + ") = " + districtHash.get(latlng));
        return districtHash.get(latlng);
    }

//    public static Bitmap findMap(String latlng) {
//        if (!mapHash.containsKey(latlng)) {
//            try {
//                JSONArray result = new APICallTask().execute("http://maps.googleapis.com/maps/api/staticmap?center=" + latlng + "&size=640x400&zoom=10&style=element:labels|visibility:off&style=element:geometry.stroke|visibility:off&style=feature:landscape|element:geometry|saturation:-100&style=feature:water|saturation:-100|invert_lightness:true").get().getJSONArray("results");
//                JSONArray subresult = result.getJSONObject(0).getJSONArray("address_components");
//                for (int i = 0; i < subresult.length(); i++) {
//                    JSONObject subobject = subresult.getJSONObject(i);
//                    if (subobject.getJSONArray("types").getString(0).equals("postal_code")) {
//                        picture = subobject.getString("short_name");
//                    }
//                }
//                mapHash.put(latlng, picture);
//            } catch (Exception e) {
//                Log.e(TAG, e.getMessage(), e);
//            }
//        }
//        Log.d(TAG, "findMap(" + latlng + ") = " + mapHash.get(latlng));
//        return mapHash.get(latlng);
//    }

    public static String findRandomZipCode(Context context) {
        if (zipcodes.isEmpty()) {
            try {
                InputStream inputStream = context.getAssets().open("postal_codes.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                Log.d(TAG, "read csv");
                String csvLine;
                while ((csvLine = reader.readLine()) != null) {
                    zipcodes.add(csvLine);
                }
                reader.close();
                Log.d(TAG, "closing reader");
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Random rand = new Random();
        String randomZip = "00000";
        boolean tilItsRight = true;
        while (tilItsRight) {
            try {
                randomZip = zipcodes.get(rand.nextInt(zipcodes.size() - 1) + 1);
                String[] coord = getLatLng(randomZip).split(",");
                JSONArray result = new APICallTask().execute("https://congress.api.sunlightfoundation.com/districts/locate?" + "latitude=" + coord[0] + "&longitude=" + coord[1]).get().getJSONArray("results");
                result.getJSONObject(0).getInt("district");
                tilItsRight = false;
            } catch (JSONException e) {
                ;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "findRandomZipCode() = " + randomZip);
        return randomZip;
    }

    public static ArrayList<Representative> findRepresentatives(String latlng, Context context) {
        if (!repHash.containsKey(latlng)) {
            try {
                String[] coord = latlng.split(",");
                TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
                Fabric.with(context, new Twitter(authConfig));

                ArrayList<Representative> output = new ArrayList<Representative>();
                JSONArray result = new APICallTask().execute("https://congress.api.sunlightfoundation.com/legislators/locate?" + "latitude=" + coord[0] + "&longitude=" + coord[1]).get().getJSONArray("results");
                for (int i = 0; i < result.length(); i++) {
                    JSONObject legislator = result.getJSONObject(i);
                    final Representative rep = new Representative(legislator.getString("first_name"), legislator.getString("last_name"), legislator.getString("party")); // party is just D or R
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

                    object = new Object();

                    TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                        @Override
                        public void success(Result<AppSession> appSessionResult) {
                            AppSession session = appSessionResult.data;
                            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                            twitterApiClient.getStatusesService().userTimeline(null, handle, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                                @Override
                                public void success(Result<List<Tweet>> listResult) {
                                    for (Tweet tweet : listResult.data) {
                                        rep.setMyLastTweet(tweet.text);
                                        Log.d("fabricstuff", "result: " + tweet.text + "  " + tweet.createdAt);
                                        synchronized (object) {
                                            object.notifyAll();
                                        }
                                    }
                                }

                                @Override
                                public void failure(TwitterException e) {
                                    e.printStackTrace();
                                    synchronized (object) {
                                        object.notifyAll();
                                    }
                                }
                            });
                        }

                        @Override
                        public void failure(TwitterException e) {
                            e.printStackTrace();
                            synchronized (object) {
                                object.notifyAll();
                            }
                        }
                    });

//                    synchronized (object) {
//                        try {
//                            object.wait();
//                            Log.d(TAG, "meow " + rep.getMyLastTweet());
//                        } catch (InterruptedException e) {
//                            Log.e("Message", "Interrupted Exception while getting lock" + e.getMessage());
//                        }
//                    }

                    output.add(rep);
                }
                repHash.put(latlng, output);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "findRepresentatives(" + latlng + ") = " + repHash.get(latlng));
        return repHash.get(latlng);
    }

    public static ArrayList<String> findVote(String latlng, int year, Context context) {
        if (!voteHash.containsKey(latlng)) {
            try {
                String countyName = "";
                JSONArray result = new APICallTask().execute("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latlng + "&result_type=administrative_area_level_2").get().getJSONArray("results");
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
                voteHash.put(latlng, output);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "findVote(" + latlng + ", " + year + ") = " + voteHash.get(latlng));
        return voteHash.get(latlng);
    }

    public static String getZipCode(String latlng) {
        if (!zipHash.containsKey(latlng)) {
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
                zipHash.put(latlng, zipcode);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "findZipCode(" + latlng + ") = " + zipHash.get(latlng));
        return zipHash.get(latlng);
    }

    public static String getLatLng(String zipcode) {
        if (!latlngHash.containsKey(zipcode)) {
            try {
                String latlng = "";
                JSONArray result = new APICallTask().execute("https://maps.googleapis.com/maps/api/geocode/json?address=" + zipcode).get().getJSONArray("results");
                JSONObject subresult = result.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                latlng += subresult.getDouble("lat") + "," + subresult.getDouble("lng");
                latlngHash.put(zipcode, latlng);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        Log.d(TAG, "findLatLng(" + zipcode + ") = " + latlngHash.get(zipcode));
        return latlngHash.get(zipcode);
    }

    public static boolean verifyZipCode(String zipcode, Context context) {
        if (!verifyHash.containsKey(zipcode)) {
            if (zipcodes.isEmpty()) {
                try {
                    InputStream inputStream = context.getAssets().open("postal_codes.csv");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    Log.d(TAG, "read csv");
                    String csvLine;
                    while ((csvLine = reader.readLine()) != null) {
                        zipcodes.add(csvLine);
                    }
                    reader.close();
                    Log.d(TAG, "closing reader");
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            boolean inSunlight = false;
            try {
                String[] coord = getLatLng(zipcode).split(",");
                JSONArray result = new APICallTask().execute("https://congress.api.sunlightfoundation.com/districts/locate?" + "latitude=" + coord[0] + "&longitude=" + coord[1]).get().getJSONArray("results");
                result.getJSONObject(0).getInt("district");
                inSunlight = true;
            } catch (JSONException e) {
                inSunlight = false;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            verifyHash.put(zipcode, zipcodes.contains(zipcode) && inSunlight);
        }
        Log.d(TAG, "VerifyZipCode(" + zipcode + ") = " + verifyHash.get(zipcode));
        return verifyHash.get(zipcode);

    }
}
