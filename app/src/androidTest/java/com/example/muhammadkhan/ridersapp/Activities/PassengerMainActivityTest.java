package com.example.muhammadkhan.ridersapp.Activities;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.example.muhammadkhan.ridersapp.Models.Passenger;
import com.example.muhammadkhan.ridersapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class PassengerMainActivityTest {
    @Rule
    public ActivityTestRule<PassengerMainActivity> passengerMainActivityActivityTestRule=new ActivityTestRule
            <PassengerMainActivity>(PassengerMainActivity.class);
    private PassengerMainActivity passengerMainActivity=null;
    private List<View> views=new ArrayList<>();
    @Before
    public void setUp() throws Exception {
        passengerMainActivity=passengerMainActivityActivityTestRule.getActivity();
    }
    @Test
    public void testLaunch(){
        View view=passengerMainActivity.findViewById(R.id.frame);
        views.add(view);
        view=passengerMainActivity.findViewById(R.id.drawer_id);
        views.add(view);
        view=passengerMainActivity.findViewById(R.id.navigation_bottom);
        views.add(view);
        //method call for checking these views
        testViews(views);
    }
    @After
    public void tearDown() throws Exception {
        passengerMainActivity=null;
    }
    //testing different views
    public void testViews(List<View> views){
        for(int i=0;i<views.size();i++){
            assertNotNull(views.get(i));
        }
    }

}