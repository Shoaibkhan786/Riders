package com.example.muhammadkhan.ridersapp.Activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.muhammadkhan.ridersapp.Fragments.BookingEmptyFragment;
import com.example.muhammadkhan.ridersapp.Fragments.DriverBookingAdpater;
import com.example.muhammadkhan.ridersapp.Fragments.DriverBookingFragment;
import com.example.muhammadkhan.ridersapp.Fragments.DriverSearch;
import com.example.muhammadkhan.ridersapp.Fragments.NotificationForDriverFragment;
import com.example.muhammadkhan.ridersapp.Fragments.ProfileFragementDriver;
import com.example.muhammadkhan.ridersapp.Fragments.ProfileFragmentPassenger;
import com.example.muhammadkhan.ridersapp.Fragments.SearchEmptyFragment;
import com.example.muhammadkhan.ridersapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.muhammadkhan.ridersapp.R.id.navigation_bottom;

public class DriverMainActivity extends AppCompatActivity {
    private android.support.v4.app.FragmentTransaction transaction;
    private AHBottomNavigation bottomNavigation;
    ProgressDialog progressDialog;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        bottomNavigation=(AHBottomNavigation) findViewById(R.id.navigation_bottom);
        //only portrait mode
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);
        //method call
        createBottomNavigation();
        //show progress bar
        showProgressbar();
        //booking fragment call
        DriverBookingFragment driverBookingFragment=new DriverBookingFragment();
        BookingEmptyFragment bookingEmptyFragment=new BookingEmptyFragment();
        userChildChecking("Booking",bookingEmptyFragment,driverBookingFragment, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
    }
    // Method for creating bottom navigation bar
    public void createBottomNavigation(){
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Home",R.mipmap.ic_home_black_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Notification",R.mipmap.ic_notifications_black_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Profile",R.mipmap.ic_person_black_24dp);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        // Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if(position==0){
                    //changing the tool bar label
                    getSupportActionBar().setTitle("Home");
                    showProgressbar();
                    DriverBookingFragment driverBookingFragment = new DriverBookingFragment();
                    BookingEmptyFragment bookingEmptyFragment=new BookingEmptyFragment();
                    userChildChecking("Booking",bookingEmptyFragment,driverBookingFragment,
                            FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                }
                if(position==1){
                    //changing the tool bar label
                    getSupportActionBar().setTitle("Notification");
                    showProgressbar();
                    NotificationForDriverFragment notificationForDriverFragment = new NotificationForDriverFragment();
                    SearchEmptyFragment searchEmptyFragment=new SearchEmptyFragment();
                    userChildChecking("Notification",searchEmptyFragment,notificationForDriverFragment,
                            FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                }
                if(position==2){
                    //changing the tool bar label
                    getSupportActionBar().setTitle("Profile");
                    ProfileFragementDriver driverProfile = new ProfileFragementDriver();
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, driverProfile);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                return true;
            }
        });

    }
    // method for checking user child
    public void userChildChecking(final String parent, final android.support.v4.app.Fragment fragmentEmpty, final android.support.v4.app.Fragment notEmpty, final String childName){
        reference= FirebaseDatabase.getInstance().getReference(parent);
        Handler handler = new Handler();
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
    public void showProgressbar(){
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
    }
    public void dismissProgressBar(){
        progressDialog.dismiss();
    }
}
