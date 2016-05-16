package com.example.shweta.sharemytrip;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class RideChoice extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_choice);


        ImageButton create = (ImageButton) findViewById(R.id.myCar);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RideChoice.this, CreateForm.class);
                intent.putExtra("CarType","MyCar");
                startActivity(intent);
            }
        });

        ImageButton join = (ImageButton) findViewById(R.id.uber);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RideChoice.this, uberActivity.class);
                intent.putExtra("CarType","Uber");
                startActivity(intent);
            }
        });
    }
}
