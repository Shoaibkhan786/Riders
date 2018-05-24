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
public class DriverProfileTest {
    @Rule
    public ActivityTestRule<DriverProfile> driverProfileActivityTestRule = new ActivityTestRule
            <DriverProfile>(DriverProfile.class);
    private DriverProfile driverProfile = null;
    private List<View> views = new ArrayList<>();
    @Before
    public void setUp() throws Exception {
        driverProfile = driverProfileActivityTestRule.getActivity();
    }
    @Test
    public void testLaunch() {
        View view = driverProfile.findViewById(R.id.first_image);       views.add(view);
        view = driverProfile.findViewById(R.id.second_image);           views.add(view);
        view = driverProfile.findViewById(R.id.third_image);            views.add(view);
        view = driverProfile.findViewById(R.id.fourth_image);           views.add(view);
        view = driverProfile.findViewById(R.id.image);                  views.add(view);
        view = driverProfile.findViewById(R.id.driver_name);            views.add(view);
        view = driverProfile.findViewById(R.id.space);                  views.add(view);
        view = driverProfile.findViewById(R.id.price_per_kilo);         views.add(view);
        view = driverProfile.findViewById(R.id.model_name);             views.add(view);
        view = driverProfile.findViewById(R.id.register);               views.add(view);
        view = driverProfile.findViewById(R.id.spinner);                views.add(view);
        //method call for checking these views
        testViews(views);
    }
    //testing different views
    public void testViews(List<View> views) {
        for (int i = 0; i < views.size(); i++) {
            assertNotNull(views.get(i));
        }
    }
    @After
    public void tearDown() throws Exception {
        driverProfile = null;
    }

}