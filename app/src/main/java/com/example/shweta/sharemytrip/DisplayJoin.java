package com.example.shweta.sharemytrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class DisplayJoin extends ListActivity {
    private static final String TAG = "ChooseRoute";
    private static final String TAG_SOURCEDES = "SOURCEDES";
    private static final String TAG_SEATS = "SEATS";
    private static final String TAG_TIME = "TIME";
    ArrayList<HashMap<String, String>> joinRide;
    HashMap<Integer, JSONObject> finalSend;

    String joinTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_join);

        joinRide = new ArrayList<HashMap<String, String>>();
        finalSend = new HashMap<Integer, JSONObject>();

        Bundle extras = getIntent().getExtras();
        String joinRoutes = (String) extras.get("Response");
        joinTime = (String) extras.get("joinTime");

        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                final JSONObject sentStr = finalSend.get(position);
                new finall().execute(sentStr);


            }
        });


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
                    Log.i(TAG, creator);
                    String source = jb.getString("source");
                    Log.i(TAG, source);
                    String rideId = jb.getString("rideId");

                    String destination = jb.getString("destination");
                    String seatsAvailable = jb.getString("seatsAvailable");
                    String requiredSeats = jb.getString("requiredSeats");
                    String firstName = jb.getJSONObject("profile").getString("firstName");
                    String lastName = jb.getJSONObject("profile").getString("lastName");


                    JSONObject jsonobj = new JSONObject();
                    jsonobj.put("creator", creator);
                    jsonobj.put("seatsAvailable", seatsAvailable);
                    jsonobj.put("rideId", rideId);
                    jsonobj.put("source", source);
                    jsonobj.put("destination", destination);
                    jsonobj.put("UserProfile", jb.getJSONObject("profile"));
                    jsonobj.put("requiredSeats", requiredSeats);
                    Log.d(TAG, jsonobj.toString());


                    finalSend.put(i, jsonobj);
                    HashMap<String, String> joinMap2 = new HashMap<String, String>();

                    joinMap2.put(TAG, "CREATOR NAME : " + firstName + lastName);
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
            SimpleAdapter adapter = new SimpleAdapter(DisplayJoin.this, joinRide, R.layout.activity_list, new String[]{TAG, TAG_SOURCEDES, TAG_SEATS, TAG_TIME, TAG}, new int[]{R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7});
            setListAdapter(adapter);


        }
    }

    private class finall extends AsyncTask<JSONObject, Void, String> {
        String response = "";

        @Override
        protected String doInBackground(JSONObject... params) {

            try {

                JSONObject selectRoute = params[0];
                URL url = new URL("http://ec2-user@ec2-54-175-188-250.compute-1.amazonaws.com:8080/RideShare/acceptRide");
                Log.d(TAG, "DISPLAY JOIN 1");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                Log.d(TAG, "DISPLAY JOIN 2");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-type", "application/json");

                connection.connect();
                Log.d(TAG, "DISPLAY JOIN 3");


                DataOutputStream printout = new DataOutputStream(connection.getOutputStream());

                printout.write(selectRoute.toString().getBytes("UTF8"));
                printout.flush();

                int responseCode = connection.getResponseCode();
                Log.i(TAG, "POST Response Code :: " + responseCode);

                if (responseCode == HttpsURLConnection.HTTP_OK) {


                    String line = "";
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();

                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");

                    }
                    response = sb.toString();
                    Log.i(TAG, response);

                } else {
                    Log.e(TAG, "No Response");
                }


            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "YOUR REQUEST HAS BEEN SENT TO THE CREATOR.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), Profile.class);
            startActivity(intent);
        }
    }


}
