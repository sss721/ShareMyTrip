package com.example.shweta.sharemytrip;

import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DisplayJoin extends ListActivity {
    private static final String TAG = "ChooseRoute";
    private static final String TAG_SOURCEDES = "SOURCEDES";
    private static final String TAG_SEATS = "SEATS";
    private static final String TAG_TIME = "TIME";
    ArrayList<HashMap<String, String>> joinRide;
    String joinTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_join);

        Bundle extras = getIntent().getExtras();
        String joinRoutes = (String) extras.get("retResponse");
        joinTime = (String) extras.get("joinTime");

        new JoinRoutes().execute(joinRoutes);
    }

    private class JoinRoutes extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String str = params[0];

                Log.i("jsonobject", str);

                JSONArray jarr = new JSONArray(str);
                for (int i = 0; i < jarr.length(); i++) {

                    JSONObject jb = jarr.getJSONObject(i);
                    String creator = jb.getString("creator");
                    String source = jb.getString("source");
                    String destination = jb.getString("destination");
                    String seatsAvailable = jb.getString("seatsAvailable");

                    String firstName = jb.getJSONObject("profile").getString("firstName");
                    String lastName =jb.getJSONObject("profile").getString("lastName");


                    HashMap<String, String> joinMap2 = new HashMap<String, String>();

                    joinMap2.put(TAG, "CREATOR : " + firstName + lastName);
                    joinMap2.put(TAG, "CREATOR ID : " + creator);
                    joinMap2.put(TAG_SOURCEDES, "ROUTE : " + source + " -->" + destination);
                    joinMap2.put(TAG_SEATS, "SEATS AVAILABLE : " + seatsAvailable);
                    joinMap2.put(TAG_TIME, "TIME : " + joinTime);

                    joinRide.add(joinMap2);

                    // String total = "ROUTE " + "" + (i+1)+ '\n' + "DISTANCE : " + distanceCovered + '\n'+ "TIME : " + timeTaken;


                    // listDataChild.put(listDataHeader.get(i), route_addresses);


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String response) {
            SimpleAdapter adapter = new SimpleAdapter(DisplayJoin.this, joinRide, R.layout.activity_list, new String[]{TAG, TAG_SOURCEDES,TAG_SEATS, TAG_TIME,TAG}, new int[]{R.id.textView3, R.id.textView4, R.id.textView5,R.id.textView6,R.id.textView7});
            setListAdapter(adapter);


        }
    }




}
