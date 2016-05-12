package com.example.shweta.sharemytrip;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class ChooseRoute extends ListActivity {
    private static final String TAG = "ChooseRoute";
    private static final String TAG_DISTANCE = "DISTANCE";
    private static final String TAG_TIME = "TIME";
    ArrayList<HashMap<String, String>> routesList;
    Context context;
    SharedPreferences sharedpreferences;
    HashMap<Integer, String> addressMap;
    HashMap<Integer, JSONObject> addressSend;

    String formatAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routesList = new ArrayList<HashMap<String, String>>();
        addressMap = new HashMap<Integer, String>();
        addressSend = new HashMap<Integer, JSONObject>();
        setContentView(R.layout.activity_choose_route);

        Bundle extras = getIntent().getExtras();
        String ext = (String) extras.get("Response");


        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("position", "" + position);
                final String rt = addressMap.get(position);
                final JSONObject sentStr = addressSend.get(position);
                new AlertDialog.Builder(ChooseRoute.this)
                        .setTitle("RIDE DETAILS")
                        .setMessage(rt)
                        .setPositiveButton("BOOK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Log.i("check for this link","" + sentStr);

                                new SendData().execute(sentStr);


                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();

            }
        });


        new HttpAsyncTask().execute(ext);
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String str = params[0];

                //Log.i(TAG, str);
                //List<String> route_addresses = new ArrayList<>();
                JSONArray jarr = new JSONArray(str);
                for (int i = 0; i < jarr.length(); i++) {
                    //String address = "";
                    JSONObject jb = jarr.getJSONObject(i);
                    String i_add = jb.getString("address");
                    String distanceCovered = jb.getJSONObject("distance").getString("humanReadable");
                    String timeTaken = jb.getJSONObject("timetaken").getString("humanReadable");
                    JSONArray add = new JSONArray(i_add);

                    Log.i("..........", "" + i);

                    String address = "";

                    for (int j = 0; j < add.length(); j++) {

                        address = address + add.getString(j) + '\n' ;


                    }


                    HashMap<String, String> map2 = new HashMap<String, String>();
                    map2.put(TAG, "ROUTE " + "" + (i + 1));
                    map2.put(TAG_DISTANCE, "DISTANCE : " + distanceCovered);
                    map2.put(TAG_TIME, "TIME : " + timeTaken);




                    addressMap.put(i, address);
                    addressSend.put(i,jarr.getJSONObject(i));


                    routesList.add(map2);

                    // String total = "ROUTE " + "" + (i+1)+ '\n' + "DISTANCE : " + distanceCovered + '\n'+ "TIME : " + timeTaken;


                    // listDataChild.put(listDataHeader.get(i), route_addresses);


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String response) {
            SimpleAdapter adapter = new SimpleAdapter(ChooseRoute.this, routesList, R.layout.activity_list, new String[]{TAG, TAG_DISTANCE, TAG_TIME}, new int[]{R.id.textView3, R.id.textView4, R.id.textView5});
            setListAdapter(adapter);


        }
    }

    private class SendData extends AsyncTask<JSONObject, Void, String> {
        String response = "";

        @Override
        protected String doInBackground(JSONObject... params) {

            try {

                JSONObject selectRoute = params[0];
                URL url = new URL("http://ec2-52-91-16-146.compute-1.amazonaws.com:8080/RideShare/newpoolrequest");
                Log.d(TAG, "CHOOSE ROUTE 1");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                Log.d(TAG, "CHOOSE ROUTE 2");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-type", "application/json");

                connection.connect();
                Log.d(TAG, "CHOOSE ROUTE 3");


                DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
                SharedPreferences sharedpreferences = getSharedPreferences("key", MODE_PRIVATE);
                String jsonData = (sharedpreferences.getString("jsonObject", ""));
                String carType = (sharedpreferences.getString("CarType", ""));
                String userId = (sharedpreferences.getString("userName", ""));

                Log.i("SEND DATA PART", jsonData + carType + userId);


                JSONObject jsonobj = new JSONObject(jsonData);
                jsonobj.put("carType", carType);
                jsonobj.put("userId", userId);
                jsonobj.put("selectRoute", selectRoute);

                Log.i("THIS IS THE TRUE LIN", jsonobj.toString());


                printout.write(jsonobj.toString().getBytes("UTF8"));
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
            } catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception", e);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Ride added successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), Profile.class);
            startActivity(intent);
        }
    }


}
