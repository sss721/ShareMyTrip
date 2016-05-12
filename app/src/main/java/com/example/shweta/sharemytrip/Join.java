package com.example.shweta.sharemytrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import model.BookTrip;

public class Join extends Activity {
    private static final String TAG = "JoinActivity";
    BookTrip joinObj;
    EditText joinDate, joinTime, passengers, source;
    TimePickerDialog timePickerDialog;
    Calendar joinCalendarDate;
    ImageButton btnShowLocation;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        btnShowLocation = (ImageButton) findViewById(R.id.geoLocation);


        btnShowLocation.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                gps = new GPSTracker(Join.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    String value = geoLoc(latitude, longitude, 1);
                    source = (EditText) findViewById(R.id.src);
                    source.setText(value);
                } else {
                    gps.showSettingsAlert();
                }
            }
        });


        joinDate = (EditText) findViewById(R.id.joinDate);
        joinDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar currentDate = Calendar.getInstance();
                int currentYear = currentDate.get(Calendar.YEAR);
                int currentMonth = currentDate.get(Calendar.MONTH);
                int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker = new DatePickerDialog(Join.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                        joinCalendarDate = Calendar.getInstance();
                        joinCalendarDate.set(selectedyear, selectedmonth, selectedday);
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                        joinDate.setText(format1.format(joinCalendarDate.getTime()));
                        joinDate.clearFocus();
                    }
                }, currentYear, currentMonth, currentDay);

                mDatePicker.setTitle("            Join Trip            ");
                mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mDatePicker.show();

            }
        });


        joinTime = (EditText) findViewById(R.id.joinTime);
        joinTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                joinTime.setVisibility(View.VISIBLE);
                openTimePicker(false);

            }
        });



        ImageButton searchRide = (ImageButton) findViewById(R.id.look);
        searchRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                joinObj = searchCar();
                new searchNearBy().execute();
            }
        });

        passengers = (EditText) findViewById(R.id.seatsAvailable);
        passengers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NumberPicker numberPicker = new NumberPicker(Join.this);
                numberPicker.setMaxValue(50);
                numberPicker.setMinValue(0);
                NumberPicker.OnValueChangeListener valueChangeListener = new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int OldVal, int newVal) {
                        passengers.setText("" + newVal);
                    }
                };
                numberPicker.setOnValueChangedListener(valueChangeListener);
                AlertDialog.Builder builder = new AlertDialog.Builder(Join.this).setView(numberPicker);
                builder.setTitle("SEATS AVAILABLE");
                builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                    }

                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                        .show();

            }
        });


    }

    public String geoLoc(Double lat, Double lon, int value) {
        Geocoder geocoder;
        List<Address> addresses;
        String addSrc = "";
        geocoder = new Geocoder(this, Locale.getDefault());

        try {

            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String postalCode = addresses.get(0).getPostalCode();


            addSrc = address + " " + city + " " + state + " " + postalCode;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return  addSrc;

    }

    private void openTimePicker(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(
                Join.this,
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

            joinTime.setText(calSet.getTime().getHours() + ":" + calSet.getTime().getMinutes());



        }
    };

    public BookTrip searchCar(){
        String join_date_time = "";
        BookTrip srcDes = new BookTrip();

        EditText source = (EditText) findViewById(R.id.src);
        String uSource = source.getText().toString();
        srcDes.setSource(uSource);

        EditText destination = (EditText) findViewById(R.id.des);
        String uDestination = destination.getText().toString();
        srcDes.setDestination(uDestination);

        EditText date = (EditText) findViewById(R.id.joinDate);
        String uDate = date.getText().toString();
        srcDes.setDate(uDate);

        join_date_time = join_date_time + uDate;

        EditText time = (EditText) findViewById(R.id.joinTime);
        String uTime = time.getText().toString();
        srcDes.setDate(uTime);
        join_date_time = join_date_time + " " + uTime;
        srcDes.setDate(join_date_time);

        EditText numberOfPassengers = (EditText) findViewById(R.id.seatsAvailable);
        String uPassengers = numberOfPassengers.getText().toString();
        int pass = Integer.parseInt(uPassengers);
        srcDes.setNumberOfPassengers(pass);




        return srcDes;
    }

    class searchNearBy extends AsyncTask<String, String, String> {
        String retResponse = "";

        @Override
        protected String doInBackground(String... params) {
            try {

                URL url = new URL("http://ec2-52-91-16-146.compute-1.amazonaws.com:8080/RideShare/searchCarForPooling");
                Log.d(TAG, "CONNECTION IN JOIN ACTIVITY");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                Log.d(TAG, "CONNECTION IN JOIN ACTIVITY 1");

                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-type", "application/json");

                connection.connect();
                Log.d(TAG, "CONNECTION IN JOIN ACTIVITY 2");

                SharedPreferences sharedpreferences = getSharedPreferences("key", MODE_PRIVATE);
                String userId = (sharedpreferences.getString("userName", ""));



                try {
                    JSONObject jsonobj = new JSONObject();
                    jsonobj.put("source", joinObj.getSource());
                    jsonobj.put("destination", joinObj.getDestination());
                    jsonobj.put("date", joinObj.getDate());
                    jsonobj.put("userId", userId);
                    jsonobj.put("seatsAvailable",joinObj.getNumberOfPassengers());

                    Log.d(TAG, jsonobj.toString());



                    DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
                    printout.write(jsonobj.toString().getBytes("UTF8"));

                    printout.flush();

                    int responseCode = connection.getResponseCode();
                    Log.i(TAG, "POST Response Code :: " + responseCode);

                    if (responseCode == HttpsURLConnection.HTTP_OK) {


                        String rLine = "";
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder sb = new StringBuilder();

                        while ((rLine = br.readLine()) != null) {
                            sb.append(rLine + "\n");

                        }
                        retResponse = sb.toString();
                        if(retResponse == null){
                            Toast.makeText(getBaseContext(), "NO RIDES WITH SELECTED SOURCE AND DESTINATION!", Toast.LENGTH_SHORT).show();
                            Intent i= new Intent(getBaseContext(), Profile.class);
                            startActivity(i);
                        }
                        Log.i(TAG, retResponse);

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
            return retResponse;

        }


        @Override
        protected void onPostExecute(String result) {

            Intent intent= new Intent(getBaseContext(), DisplayJoin.class);
            intent.putExtra("Response", retResponse);
            intent.putExtra("joinTime", joinTime.getText().toString());
            startActivity(intent);
        }
    }


}
