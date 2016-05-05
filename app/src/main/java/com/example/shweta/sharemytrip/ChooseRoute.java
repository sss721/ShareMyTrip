package com.example.shweta.sharemytrip;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChooseRoute extends Activity {
    private static final String TAG = "ChooseRoute";
    private static final String TAG_DISTANCE = "DISTANCE";
    private static final String TAG_TIME = "TIME";
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    List<String> route_addresses;
    int i;

    HashMap<String, String>  listDataChild;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_route);
        listDataHeader = new ArrayList<>();
        route_addresses = new ArrayList();

        Bundle extras = getIntent().getExtras();
        String ext = (String) extras.get("Response");

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandableList);

        // preparing list data
        new HttpAsyncTask().execute(ext);

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);


        expListView.setAdapter(listAdapter);
        // setting list adapter

        // ListView lv = getListView();

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            try {
                String str = params[0];

                //Log.i(TAG, str);
                //List<String> route_addresses = new ArrayList<>();
                JSONArray jarr = new JSONArray(str);
                for(i = 0; i < jarr.length(); i++) {
                    //String address = "";
                    JSONObject jb =jarr.getJSONObject(i);
                    String i_add = jb.getString("address");
                    String distanceCovered = jb.getJSONObject("distance").getString("inMeters");
                    String timeTaken = jb.getJSONObject("timetaken").getString("inSeconds");
                    JSONArray add = new JSONArray(i_add);

                    Log.i("..........", "" + i);
                    String address = "";
                    for (int j = 0; j < add.length(); j++) {

                        address = address +add.getString(j);
                    }
                    route_addresses.add(address);
                    Log.i("..........", "" + route_addresses);

                   /* HashMap<String, String> map2 = new HashMap<String, String>();
                    map2.put(TAG,"ROUTE " + "" + (i+1));
                    map2.put(TAG_DISTANCE, "DISTANCE : " + distanceCovered);
                    map2.put(TAG_TIME, "TIME : " + timeTaken);
                    listDataHeader.add(map2);
                    */
                    String total = "ROUTE " + "" + (i+1)+ '\n' + "DISTANCE : " + distanceCovered + '\n'+ "TIME : " + timeTaken;
                    listDataHeader.add(total);

                   // listDataChild.put(listDataHeader.get(i), route_addresses);


                }
                 Log.i(TAG, route_addresses.get(0));
                 for(int m = 0; m < i; m++){
                     listDataChild.put(listDataHeader.get(m),route_addresses.get(m));
                 }


            }catch(Exception e)
            {
                e.printStackTrace();
            }
         return null;
        }
        protected void onPostExecute(String response) {
           // SimpleAdapter adapter = new SimpleAdapter(ChooseRoute.this, list, R.layout.activity_list, new String[]{TAG, TAG_DISTANCE, TAG_TIME}, new int[]{R.id.textView3,R.id.textView4, R.id.textView5});
           // setListAdapter(adapter);

            Toast.makeText(getApplicationContext(),"Check out routes",
                    Toast.LENGTH_SHORT).show();


        }
    }


}
