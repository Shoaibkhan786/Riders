package com.example.muhammadkhan.ridersapp.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.muhammadkhan.ridersapp.Fragments.BookingEmptyFragment;
import com.example.muhammadkhan.ridersapp.Fragments.BookingFragment;
import com.example.muhammadkhan.ridersapp.Fragments.DriverSearch;
import com.example.muhammadkhan.ridersapp.Fragments.NavigationDrawerTopFragment;
import com.example.muhammadkhan.ridersapp.Fragments.ProfileFragmentPassenger;
import com.example.muhammadkhan.ridersapp.Fragments.SearchEmptyFragment;
import com.example.muhammadkhan.ridersapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

import static com.example.muhammadkhan.ridersapp.R.id.navigation_bottom;

public class PassengerMainActivity extends AppCompatActivity {
    DatabaseReference reference;
    Location mLocation;
    String currentCity;
    LocationManager locationManager;
    FrameLayout frame;
    ProgressDialog progressDialog;
    AHBottomNavigation bottomNavigation;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_activity_main);
        //only portrait mode
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);
        //background for getting currrent location of the user
        Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                getCurrentLocation();
            }
        });
        //method call
        createBottomNavigation();
        frame=(FrameLayout) findViewById(R.id.frame);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_id);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //method call
        showProgressbar();
        BookingFragment booking=new BookingFragment();
        BookingEmptyFragment emptyFragment=new BookingEmptyFragment();
        userChildChecking("Booking",emptyFragment,booking,FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        //loading top fragment in navigation drawer
        transaction = getSupportFragmentManager().beginTransaction();
        NavigationDrawerTopFragment topFragment = new NavigationDrawerTopFragment();
        topFragment.setAttributes(mDrawerLayout,frame);
        transaction.add(R.id.fragment,topFragment);
        transaction.commit();

    }
    // method for checking user child
    public void userChildChecking(final String parent, final Fragment fragmentEmpty, final Fragment notEmpty, final String childName){
            reference= FirebaseDatabase.getInstance().getReference(parent);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(childName)) {
                                System.out.println("bacha mill giya");
                                //loading booked fragment into Main activity
                                transaction = getSupportFragmentManager().beginTransaction();
                                transaction.add(R.id.frame, notEmpty);
                                transaction.commit();
                                //method call
                                dismissProgressBar();

                            } else {
                                //loading empty fragment
                                System.out.println("becha ni mila");
                                transaction = getSupportFragmentManager().beginTransaction();
                                transaction.add(R.id.frame, fragmentEmpty);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                //method call
                                dismissProgressBar();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    //-----------------------------------Method for creating bottom navigation bar-----------------------------------//
    public void createBottomNavigation() {
        bottomNavigation = (AHBottomNavigation) findViewById(navigation_bottom);
        AHBottomNavigationItem item0 = new AHBottomNavigationItem("Home",R.mipmap.ic_home_black_24dp);
       // AHBottomNavigationItem item2 = new AHBottomNavigationItem("Notification",R.mipmap.ic_notifications_black_24dp);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Booking",R.mipmap.icons8_booking_50);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Profile",R.mipmap.ic_person_black_24dp);
        bottomNavigation.addItem(item0);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        //bottomNavigation.addItem(item4);
        // Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if(position==0){
                    //changing the tool bar label
                    getSupportActionBar().setTitle("Home");
                    //method call
                    showProgressbar();
                    BookingFragment bookingFragment=new BookingFragment();
                    BookingEmptyFragment bookingEmptyFragment=new BookingEmptyFragment();
                    userChildChecking("Booking",bookingEmptyFragment,bookingFragment,FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                }
                if(position==1){
                    //changing the tool bar label
                    getSupportActionBar().setTitle("Driver Search");
                    //method call
                    showProgressbar();
                    DriverSearch driverSearch=new DriverSearch();
                    SearchEmptyFragment searchEmptyFragment=new SearchEmptyFragment();
                    //method call
                    userChildChecking("Drivers",searchEmptyFragment,driverSearch,currentCity);

                }
                if(position==2){
                    //changing the tool bar label
                    getSupportActionBar().setTitle("Profile");
                    ProfileFragmentPassenger passengerProfile=new ProfileFragmentPassenger();
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame,passengerProfile);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                return true;
            }
        });
    }
    public void getCurrentLocation() {
        locationManager=(LocationManager)this.getSystemService(this.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER )){
            LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;
            LocationParams.Builder builder = new LocationParams.Builder()
                    .setAccuracy(trackingAccuracy);
            SmartLocation.with(this).location()
                    .oneFix()
                    .config(builder.build())
                    .start(new OnLocationUpdatedListener() {

                        @Override
                        public void onLocationUpdated(Location location) {
                            mLocation = location;
                            //  Log.i("TAG",""+mLocation.getLatitude());
                            // Log.i("TAG",""+mLocation.getLongitude());
                            //method call for getting city name
                            getCityName();
                        }
                    });
        }else{
            showSettingAlert();
        }
    }

    public void getCityName(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        Log.i("TAG", ""+mLocation.getLongitude());
        Log.i("TAG",""+mLocation.getLatitude());
        try {
            addresses = geocoder.getFromLocation(mLocation.getLatitude(),mLocation.getLongitude(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && !addresses.isEmpty()) {
            currentCity = addresses.get(0).getLocality();
            SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("current_city",currentCity);
            editor.apply();
            Log.i("TAG", currentCity);
        }
    }
    public void showSettingAlert(){
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS Setting");
        alertDialog.setMessage("Current location is required for sign up! ");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        //setting negetive button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }
    public void showProgressbar(){
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
    }
    public void dismissProgressBar(){
        progressDialog.dismiss();
    }
}
