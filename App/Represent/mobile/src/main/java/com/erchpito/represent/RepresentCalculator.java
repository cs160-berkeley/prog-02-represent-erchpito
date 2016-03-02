package com.erchpito.represent;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.erchpito.common.Representative;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by erchpito on 2/3/2016.
 */
public final class RepresentCalculator {

    private static HashMap<Integer, ArrayList<Representative>> repHash = new HashMap<Integer, ArrayList<Representative>>();

    public static int findZipCode(int lat, int lon) {
        /* ---- HARDCODE ---- */
        return 94704;
    }

    public static int findRandomZipCode() {
        /* ---- HARDCODE ---- */
        return 77412;
    }

    public static String findCounty(int zipcode) {
        /* ---- HARDCODE ---- */
        return (zipcode == 94704) ? "Alameda County, CA" : "Colorado County, TX";
    }

    public static String findLocation(int zipcode) {
        /* ---- HARDCODE ---- */
        return (zipcode == 94704) ? "CA, 94704" : "TX, 77412";
    }

    public static String findCongressionalDistrict(int zipcode) {
        /* ---- HARDCODE ---- */
        return (zipcode == 94704) ? "13th Congressional District" : "10th Congressional District";
    }

    public static int findColor(int zipcode, Context context) {
        return ContextCompat.getColor(context, (zipcode == 94704) ? R.color.oldGloryBlue : R.color.oldGloryRed);
    }

    public static ArrayList<String> find2012Vote(int zipcode) {
        ArrayList<String> vote = new ArrayList<String>();

        /* ---- HARDCODE ---- */
        if (zipcode == 94704) {
            vote.add("Obama: 78.9%");
            vote.add("Romney: 18.2%");
        } else {
            vote.add("Romney: 74.3%");
            vote.add("Obama: 25.0%");
        }

        return vote;
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
            Representative SenBarbaraBoxer = new Representative("Barbara", "Levy", "Boxer", "Democratic");
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
