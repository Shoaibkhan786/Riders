package com.example.muhammadkhan.ridersapp.Activities;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.example.muhammadkhan.ridersapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Muhammad Khan on 14/05/2018.
 */
public class PassengerProfileTest {
    @Rule
    public ActivityTestRule<PassengerProfile> splashActivity=new ActivityTestRule<PassengerProfile>(PassengerProfile.class);
    private PassengerProfile passengerProfile=null;
    private List<View> views=new ArrayList<>();
    @Before
    public void setUp() throws Exception {
        passengerProfile=splashActivity.getActivity();
    }
    @Test
    public void testLaunch(){
        View view=passengerProfile.findViewById(R.id.passenger_name);
        views.add(view);
        view=passengerProfile.findViewById(R.id.current_address);
        views.add(view);
        view=passengerProfile.findViewById(R.id.dest_address);
        views.add(view);
        view=passengerProfile.findViewById(R.id.passenger_image);
        views.add(view);
        //calling method to test these views
        testViews(views);
    }
    @After
    public void tearDown() throws Exception {
    passengerProfile=null;
    }
    //testing different views
    public void testViews(List<View> views){
        for(int i=0;i<views.size();i++){
            assertNotNull(views.get(i));
        }
    }

}