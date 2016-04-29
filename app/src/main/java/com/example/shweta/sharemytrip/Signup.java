package com.example.shweta.sharemytrip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import javax.net.ssl.HttpsURLConnection;

import helper.UserDatabaseHelper;
import model.User;

public class Signup extends Activity {
    private static final String TAG = "SignUpActivity";
    EditText firstName,lastName, userName ,userPassword, address,gender,phone,aboutMe;
   // private static final String url = "http://localhost:8080/rideshare/newuser";
   // private static final String user = "";
    //private static final String passwd = "";
    ImageButton submit;
    Context cntx;
    User userObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firstName = (EditText)findViewById(R.id.fillfirstname);
        lastName = (EditText)findViewById(R.id.filllastname);
        userName = (EditText)findViewById(R.id.fillusername);
        userPassword = (EditText)findViewById(R.id.fillpassword);
        address = (EditText)findViewById(R.id.filladdress);
        gender = (EditText)findViewById(R.id.fillgender);
        phone = (EditText)findViewById(R.id.fillphone);
        aboutMe = (EditText)findViewById(R.id.fillaboutme);

        submit = (ImageButton) findViewById(R.id.imageButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userObj = registerUser();
               // saveUser(userObj);
                new Connect().execute();
        }   });
    }


    public User registerUser() {
        User user = new User();

        EditText firstName = (EditText) findViewById(R.id.fillfirstname);
        String uName = firstName.getText().toString();
        user.setFirstName(uName);

        EditText lastName = (EditText) findViewById(R.id.filllastname);
        String uLast = lastName.getText().toString();
        user.setLastName(uLast);

        EditText userName = (EditText) findViewById(R.id.fillusername);
        String uUserName = userName.getText().toString();
        user.setUserName(uUserName);

        EditText userPassword = (EditText) findViewById(R.id.fillpassword);
        String uPass = userPassword.getText().toString();
        user.setUserPassword(uPass);

        EditText address = (EditText) findViewById(R.id.filladdress);
        String uAddress = address.getText().toString();
        user.setUserAddress(uAddress);

        EditText sex = (EditText) findViewById(R.id.fillgender);
        String uSex = sex.getText().toString();
        user.setSex(uSex);

        EditText phoneNumber = (EditText) findViewById(R.id.fillphone);
        String uPhone = phoneNumber.getText().toString();
        user.setPhoneNumber(uPhone);


        EditText aboutMe = (EditText) findViewById(R.id.fillaboutme);
        String uAboutMe = aboutMe.getText().toString();
        user.setAboutMe(uAboutMe);

        SharedPreferences sp=getSharedPreferences("key", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed=sp.edit();
        ed.putString("groupID", uName);
        ed.commit();
        return user;
    }


   class Connect extends AsyncTask<String, String, String> {
        StringBuilder sb = new StringBuilder();
        String response = "";

        @Override
        protected String doInBackground(String... params) {
            try {

                String message = new JSONObject().toString();

                URL url = new URL("http://10.0.3.2:8080/RideShare/newuser");
                Log.d(TAG,"THIS SEEMS GOOD");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                Log.d(TAG, "THIS SEEMS GOOD 1");

                //connection.setReadTimeout(10000);
                //connection.setConnectTimeout(15000 /* milliseconds */);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-type", "application/json");

                connection.connect();
                Log.d(TAG, "THIS SEEMS GOOD 2");



                try {
                    JSONObject jsonobj = new JSONObject();
                    jsonobj.put("firstName", userObj.getFirstName());
                    jsonobj.put("lastName", userObj.getLastName());
                    jsonobj.put("userName", userObj.getUserName());
                    jsonobj.put("userPassword", userObj.getUserPassword());
                    jsonobj.put("userAddress", userObj.getUserAddress());
                    jsonobj.put("phoneNumber", userObj.getPhoneNumber());
                    jsonobj.put("sex", userObj.getSex());
                    jsonobj.put("aboutMe", userObj.getAboutMe());

                    Log.d(TAG, jsonobj.toString());

                    DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
                    printout.write(jsonobj.toString().getBytes("UTF8"));

                    printout.flush();
                    printout.close();


                    int responseCode = connection.getResponseCode();
                    Log.i(TAG, "POST Response Code :: " + responseCode);
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        Log.d(TAG,"THIS SEEMS GOOD 3");
                        String line = "";
                        BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line=br.readLine()) != null) {
                            response+=line;
                        }
                    }
                    else {
                        Log.e(TAG, "No Response");
                    }

                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                } catch (IOException e) {
                    e.printStackTrace();
                }



            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;

        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "User added successfully", Toast.LENGTH_LONG).show();
            Intent viewTaskIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(viewTaskIntent);
        }
    }




}