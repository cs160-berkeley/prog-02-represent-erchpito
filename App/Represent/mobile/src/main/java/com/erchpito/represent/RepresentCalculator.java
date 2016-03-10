package com.erchpito.represent;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.erchpito.common.Representative;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by erchpito on 2/3/2016.
 */
public final class RepresentCalculator {

    private static HashMap<Integer, ArrayList<Representative>> repHash = new HashMap<Integer, ArrayList<Representative>>();
    private static HashMap<Integer, String> districtHash = new HashMap<Integer, String>();
    private static HashMap<Integer, ArrayList<String>> voteHash = new HashMap<Integer, ArrayList<String>>();


    public static int findZipCode(int lat, int lon) {
        /* ---- HARDCODE ---- */
        return 94704;
    }

    public static int findRandomZipCode() {
        /* ---- HARDCODE ---- */
        return 77412;
    }

    public static String findDistrict(int zipcode) {
        if (!districtHash.containsKey(zipcode)) {
            try {
                String output = "";
                JSONArray result = new APICallTask().execute("http://congress.api.sunlightfoundation.com/districts/locate?zip=" + zipcode).get().getJSONArray("results");
                output += result.getJSONObject(0).getString("state") + ", " + zipcode + "\n";
                output += result.getJSONObject(0).getInt("district");
                if (output.substring(output.length() - 1).equals("1")) {
                    output += "st Congressional District";
                } else if (output.substring(output.length() - 1).equals("2") || output.substring(output.length() - 1).equals("3")) {
                    output += "rd Congressional District";
                } else {
                    output += "th Congressional District";
                }
                districtHash.put(zipcode, output);
            } catch (Exception e) {
                Log.e("T", e.getMessage(), e);
            }
        }
        return districtHash.get(zipcode);

        /* ---- HARDCODE ---- */
//        return (zipcode == 94704) ? "CA, 94704\n13th Congressional District" : "TX, 77412\n10th Congressional District";
    }

    public static ArrayList<String> findVote(int zipcode, int year, Context context) {
        if (!voteHash.containsKey(zipcode)) {
            try {
                JSONObject result = new APICallTask().execute("http://map.google.com?zip=" + zipcode).get();
                String countyName = result.getString("county");

                ArrayList<String> output = new ArrayList<String>();
                InputStream inputStream = context.getAssets().open("election-county-2012.json");
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();
                JSONArray read = new JSONArray(new String(buffer, "UTF-8"));
                for (int i = 0; i < result.length(); i++) {
                    JSONObject countyResult = read.getJSONObject(i);
                    if (countyResult.getString("county-name").equals(countyName)) {
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
                Log.e("T", e.getMessage(), e);
            }
        }
        return voteHash.get(zipcode);

        /* ---- HARDCODE ---- */
//        return (zipcode == 94704) ? "CA, 94704\n13th Congressional District" : "TX, 77412\n10th Congressional District";
    }

    public static ArrayList<Representative> findRepresentatives(int zipcode) {
        if (!repHash.containsKey(zipcode)) {
            try {
                ArrayList<Representative> output = new ArrayList<Representative>();
                JSONArray result = new APICallTask().execute("https://congress.api.sunlightfoundation.com/legislators/locate?" + "zip=" + zipcode).get().getJSONArray("results");
                for (int i = 0; i < result.length(); i++) {
                    JSONObject legislator = result.getJSONObject(i);
                    Representative rep = new Representative(legislator.getString("first_name"), legislator.getString("last_name"), legislator.getString("party"));
                    rep.setMyHouse(legislator.getString("chamber").equals("senate"));
                    rep.setMyTerm(legislator.getString("term_state"), legislator.getString("term_end"));
                    rep.setMySocial(legislator.getString("website"), legislator.getString("oc_email"), legislator.getString("twitter_id")); // twitter_id sans @

                    JSONArray subResult = new APICallTask().execute("https://congress.api.sunlightfoundation.com/committees?" + "member_ids=" + legislator.getString("bioguide_id")).get().getJSONArray("results");
                    for (int j = 0; j < subResult.length(); j++) {
                        rep.addCommittee(result.getJSONObject(i).getString("name"));
                    }

                    subResult = new APICallTask().execute("https://congress.api.sunlightfoundation.com/bills?" + "sponsor_ids=" + legislator.getString("bioguide_id")).get().getJSONArray("results");
                    for (int j = 0; j < subResult.length(); j++) {
                        JSONObject bill = result.getJSONObject(i);
                        rep.addBill(bill.getString("introduced_on"), bill.getInt("number"), (bill.has("short_title")) ? bill.getString("short_title") : bill.getString("official_title"));
                    }

                    rep.setMyPortrait();
                    rep.setMyLastTweet();

                    output.add(rep);
                }
                repHash.put(zipcode, output);
            } catch (Exception e) {
                Log.e("T", e.getMessage(), e);
            }
        }
        return repHash.get(zipcode);
    }

    public static int findColor(int zipcode, Context context) {
        return ContextCompat.getColor(context, (zipcode == 94704) ? R.color.oldGloryBlue : R.color.oldGloryRed);
    }

    public static ArrayList<Representative> findRepresentatives(int zipcode) {
        if (repHash.containsKey(zipcode)) {
            Log.d("T", "hashing");
            return repHash.get(zipcode);
        }

        ArrayList<Representative> representatives = new ArrayList<Representative>();

        /* ---- HARDCODE ---- */
        if (zipcode == 94704) {
            // Barbara Boxer
            SenBarbaraBoxer.addBill("Feb. 3, 2016", 2487, "Female Veteran Suicide Prevention Act");
            representatives.add(SenBarbaraBoxer);

            // Dianne Feinstein
            Representative SenDianneFeinstein = new Representative("Dianne", "Goldman", "Feinstein", "Democratic");
            SenDianneFeinstein.setMyHouse(true);
            SenDianneFeinstein.setMyPortrait(R.drawable.feinstein);
            SenDianneFeinstein.setMyTerm("Jan. 3, 2012", "Jan. 3, 2018");
            SenDianneFeinstein.setMySocial("feintsein.senate.gov", "sendiannefeinstein@gmail.com", "@SenFeinstein");
            SenDianneFeinstein.addCommittee("Committee on Appropriations");
            SenDianneFeinstein.addCommittee("Committee on the Judicary");
            SenDianneFeinstein.addCommittee("Committee on Rules and Administration");
            SenDianneFeinstein.addCommittee("Select Committee on Intelligence");
            SenDianneFeinstein.addBill("Feb. 23, 2016", 2568, "California Desert Conservation, Off-Road Recreation, and Renewable Energy Act");
            SenDianneFeinstein.addBill("Feb. 11, 2016", 2552, "Interstate Threats Clarification Act of 2016");
            SenDianneFeinstein.addBill("Feb. 10, 2016", 2533, "California Long-Term Provisions for Water Supply and Short-Term Provisions for Emergency Drought Relief Act");
            SenDianneFeinstein.addBill("Jan. 12, 2016", 2442, "A bill to authorize the use of passenger facility charges at an airport previously associated with the airport at which the charges are collected");
            SenDianneFeinstein.addBill("Jan. 20, 2016", 2422, "Fiscal Year 2016 Department of Veterans Affairs Seismic Safety and Construction Authorization Act");
            representatives.add(SenDianneFeinstein);

            // Barbara Lee
            Representative RepBarbaraLee = new Representative("Barbara", "Jean", "Lee", "Democratic");
            RepBarbaraLee.setMyHouse(false);
            RepBarbaraLee.setMyPortrait(R.drawable.lee);
            RepBarbaraLee.setMyTerm("Jan. 3, 2015", "Jan. 3, 2017");
            RepBarbaraLee.setMySocial("lee.house.gov", "repbarbaralee@gmail.com", "@RepBarbaraLee");
            RepBarbaraLee.addCommittee("Committee on Appropriations");
            RepBarbaraLee.addCommittee("House Committee on The Budget");
            representatives.add(RepBarbaraLee);
        } else {
            // Rafael Cruz
            Representative SenBarbaraBoxer = new Representative("Rafael", "Edward", "Cruz", "Republican");
            SenBarbaraBoxer.setMyHouse(true);
            SenBarbaraBoxer.setMyPortrait(R.drawable.boxer);
            SenBarbaraBoxer.setMyTerm("Jan. 3, 2010", "Jan. 3, 2016");
            SenBarbaraBoxer.setMySocial("boxer.senate.gov", "senbarbaraboxer@gmail.com", "@SenatorBoxer");
            SenBarbaraBoxer.addCommittee("Committee on Commerce, Science, and Transportation");
            SenBarbaraBoxer.addCommittee("Committee on Environment and Public Works");
            SenBarbaraBoxer.addCommittee("Committee on Foreign Relations");
            SenBarbaraBoxer.addCommittee("Select Committee on Ethics");
            SenBarbaraBoxer.addBill("Feb. 3, 2016", 2487, "Female Veteran Suicide Prevention Act");
            representatives.add(SenBarbaraBoxer);

            // Dianne Feinstein
            Representative SenDianneFeinstein = new Representative("John", "Potato", "Cornyn", "Republican");
            SenDianneFeinstein.setMyHouse(true);
            SenDianneFeinstein.setMyPortrait(R.drawable.feinstein);
            SenDianneFeinstein.setMyTerm("Jan. 3, 2012", "Jan. 3, 2018");
            SenDianneFeinstein.setMySocial("feintsein.senate.gov", "sendiannefeinstein@gmail.com", "@SenFeinstein");
            SenDianneFeinstein.addCommittee("Committee on Appropriations");
            SenDianneFeinstein.addCommittee("Committee on the Judicary");
            SenDianneFeinstein.addCommittee("Committee on Rules and Administration");
            SenDianneFeinstein.addCommittee("Select Committee on Intelligence");
            SenDianneFeinstein.addBill("Feb. 23, 2016", 2568, "California Desert Conservation, Off-Road Recreation, and Renewable Energy Act");
            SenDianneFeinstein.addBill("Feb. 11, 2016", 2552, "Interstate Threats Clarification Act of 2016");
            SenDianneFeinstein.addBill("Feb. 10, 2016", 2533, "California Long-Term Provisions for Water Supply and Short-Term Provisions for Emergency Drought Relief Act");
            SenDianneFeinstein.addBill("Jan. 12, 2016", 2442, "A bill to authorize the use of passenger facility charges at an airport previously associated with the airport at which the charges are collected");
            SenDianneFeinstein.addBill("Jan. 20, 2016", 2422, "Fiscal Year 2016 Department of Veterans Affairs Seismic Safety and Construction Authorization Act");
            representatives.add(SenDianneFeinstein);

            // Barbara Lee
            Representative RepBarbaraLee = new Representative("Michael", "Thomas", "McCaul", "Republican");
            RepBarbaraLee.setMyHouse(false);
            RepBarbaraLee.setMyPortrait(R.drawable.lee);
            RepBarbaraLee.setMyTerm("Jan. 3, 2015", "Jan. 3, 2017");
            RepBarbaraLee.setMySocial("lee.house.gov", "repbarbaralee@gmail.com", "@RepBarbaraLee");
            RepBarbaraLee.addCommittee("Committee on Appropriations");
            RepBarbaraLee.addCommittee("House Committee on The Budget");
            representatives.add(RepBarbaraLee);
        }

        repHash.put(zipcode, representatives);
        return representatives;
    }


}
