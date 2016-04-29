package com.example.shweta.sharemytrip;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.net.ssl.HttpsURLConnection;

import model.BookTrip;

public class CreateForm extends Activity {
    private static final String TAG = "CreateFormActivity";
    Calendar tripCalendarDate;
    EditText dueDate, time;
    BookTrip userObj;
    TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_form);


        dueDate = (EditText) findViewById(R.id.fillDate);
        dueDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar currentDate = Calendar.getInstance();
                int currentYear = currentDate.get(Calendar.YEAR);
                int currentMonth = currentDate.get(Calendar.MONTH);
                int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker = new DatePickerDialog(CreateForm.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        tripCalendarDate = Calendar.getInstance();
                        tripCalendarDate.set(selectedyear, selectedmonth, selectedday);
                        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
                        dueDate.setText(format1.format(tripCalendarDate.getTime()));
                        dueDate.clearFocus();
                    }
                }, currentYear, currentMonth, currentDay);

                mDatePicker.setTitle("            Book Trip            ");
                mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mDatePicker.show();

            }
        });


        time = (EditText) findViewById(R.id.fillTime);
        time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                time.setVisibility(View.VISIBLE);
                openTimePickerDialog(false);

            }
        });
        ImageButton submit = (ImageButton) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getBaseContext(), "Trip was Created Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateForm.this, Profile.class);
                startActivity(intent);
                userObj = registerUser();
                new Connect().execute();


            }
        });

        ImageButton cancel = (ImageButton) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "Trip was Cancelled", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CreateForm.this, Profile.class);
                startActivity(intent);
            }
        });

    }


    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(
                CreateForm.this,
                onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24r);
        timePickerDialog.setTitle("            TIME             ");
        timePickerDialog.show();


    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                //Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1);
            }

            time.setText(calSet.getTime().getHours() + ":" + calSet.getTime().getMinutes());

        }
    };

    public BookTrip registerUser() {
        BookTrip book = new BookTrip();

        EditText source = (EditText) findViewById(R.id.pickup);
        String uSource = source.getText().toString();
        book.setSource(uSource);

        EditText destination = (EditText) findViewById(R.id.destination);
        String uDestination = destination.getText().toString();
        book.setDestination(uDestination);


        EditText date = (EditText) findViewById(R.id.fillDate);
        String uDate = date.getText().toString();
        book.setDate(uDate);

        EditText numberOfPassengers = (EditText) findViewById(R.id.numberOfPassengers);
        String uPassengers = numberOfPassengers.getText().toString();
        userObj.setNumberOfPassengers(uPassengers);

        SharedPreferences sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("groupID", uSource);
        ed.commit();
        return book;
    }

    class Connect extends AsyncTask<String, String, String> {
        String response = "";

        @Override
        protected String doInBackground(String... params) {
            try {


                URL url = new URL("http://10.0.3.2:8080/RideShare/searchRoutesForNewCarPoolRequest");
                Log.d(TAG, "THIS SEEMS GOOD");
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
                    jsonobj.put("firstName", userObj.getSource());
                    jsonobj.put("lastName", userObj.getDestination());
                    jsonobj.put("userId", userObj.getNumberOfPassengers());
                    jsonobj.put("userPassword", userObj.getDate());

                    Log.d(TAG, jsonobj.toString());

                    DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
                    printout.write(jsonobj.toString().getBytes("UTF8"));

                    printout.flush();
                    printout.close();


                    int responseCode = connection.getResponseCode();
                    Log.i(TAG, "POST Response Code :: " + responseCode);
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        Log.d(TAG, "THIS SEEMS GOOD 3");
                        String line = "";
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            response += line;
                        }
                    } else {
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