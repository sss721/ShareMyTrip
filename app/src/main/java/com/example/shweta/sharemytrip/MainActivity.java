package com.example.shweta.sharemytrip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.sql.SQLDataException;

import helper.UserDatabaseHelper;
import model.User;


public class MainActivity extends Activity {

    EditText username, password;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText)findViewById(R.id.put_email);
        password = (EditText)findViewById(R.id.put_passwd);

        ImageButton login = (ImageButton) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().matches("")|| password.getText().toString().matches("")){
                    Toast.makeText(getBaseContext(), "Email and Password Required ", Toast.LENGTH_LONG).show();
                }
               else if(validateLogin(username.getText().toString(), password.getText().toString(),MainActivity.this)){

                    Toast.makeText(getBaseContext(), "NEW USER!! PLEASE SIGNUP ", Toast.LENGTH_LONG).show();

                }
               else {
                    Intent intent = new Intent(MainActivity.this, Profile.class);
                    sharedpreferences = getSharedPreferences("key", Context.MODE_PRIVATE);
                    SharedPreferences.Editor ed=sharedpreferences.edit();
                    ed.putString("userName", username.getText().toString());
                    ed.commit();
                    startActivity(intent);

                }
            }
        });


        ImageButton signup = (ImageButton) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Signup.class);
                startActivity(intent);
            }
        });



    }

    public boolean validateLogin(String userName, String userPass, Context context) {

            UserDatabaseHelper database = new UserDatabaseHelper(getApplicationContext(), null, null,1);
            SQLiteDatabase db = database.getReadableDatabase();

            //SELECT
            String[] columns = {"_Id"};

            //WHERE clause
            String selection = "uUserName=? AND uPass=?";

            //WHERE clause arguments
            String[] selectionArgs = {userName, userPass};
            Cursor c = null;

            try{
                //SELECT userId FROM login WHERE username=userName AND password=userPass
                c = db.query(UserDatabaseHelper.USER_DATABASE_NAME, columns, selection, selectionArgs, null, null, null);
                c.moveToFirst();

                int i = c.getCount();
                c.close();
                if(i <= 0){

                    return true;
                }

            }catch(Exception e){
                e.printStackTrace();

            }

        return false;
    }

}
