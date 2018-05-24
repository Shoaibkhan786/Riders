package com.example.muhammadkhan.ridersapp.Activities;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muhammadkhan.ridersapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {
    private TextView riders;
    private TextView welcome;
    private Snackbar snackbar;
    private LinearLayout linearLayout;
    boolean connected = false;
    private static int SPLASH_TIME_OUT = 2000;
    FirebaseAuth auth;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        riders=(TextView) findViewById(R.id.riders);
        welcome=(TextView) findViewById(R.id.weclome);
        linearLayout=(LinearLayout) findViewById(R.id.snack_bar);
        Animation animation= AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        riders.setAnimation(animation);
        welcome.setAnimation(animation);
        //making thread sleep for well
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // if conenction are stable
                if(isConnectedToInternet()) {
                    //if user is already registered
                    auth = FirebaseAuth.getInstance();
                    if (auth.getInstance().getCurrentUser() != null) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Splash.this);
                        String key = preferences.getString("key", "");
                        if(key.equals("AS Driver")){
                            intent = new Intent(Splash.this,DriverMainActivity.class);
                            startActivity(intent);
                        }else {
                            intent = new Intent(Splash.this,PassengerMainActivity.class);
                            startActivity(intent);
                        }
                    }else{
                        intent = new Intent(Splash.this, DriverPassenger.class);
                        startActivity(intent);
                    }
                }
                else{
                    showSnackBar("No internet Connection",linearLayout);
                }
            }
        }, SPLASH_TIME_OUT);
    }

    //Method for checking network status
    private boolean isConnectedToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    //show snackbar
    public void showSnackBar(String string, LinearLayout linearLayout)
    {
        snackbar = Snackbar
                .make(linearLayout, string, Snackbar.LENGTH_INDEFINITE).
                        setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                                finish();
                            }
                        });
        snackbar.show();
    }
}
