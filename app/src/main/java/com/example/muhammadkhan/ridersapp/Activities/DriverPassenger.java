package com.example.muhammadkhan.ridersapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.muhammadkhan.ridersapp.R;

public class DriverPassenger extends AppCompatActivity {
    private Button driver;
    private Button passenger;
    Intent intent;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_passenger);
        driver=(Button) findViewById(R.id.driver_city);
        passenger=(Button) findViewById(R.id.passenger);

        passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saving button text in shared preference
                preferences= PreferenceManager.getDefaultSharedPreferences(DriverPassenger.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("key",passenger.getText().toString().trim());
                editor.apply();

                intent=new Intent(DriverPassenger.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saving button text in shared preference
                preferences= PreferenceManager.getDefaultSharedPreferences(DriverPassenger.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("key",driver.getText().toString().trim());
                editor.apply();
                //intent to login activity
                intent=new Intent(DriverPassenger.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
