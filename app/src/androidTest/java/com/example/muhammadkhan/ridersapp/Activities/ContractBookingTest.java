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
public class ContractBookingTest {
    @Rule
    public ActivityTestRule<ContractBooking> contractBookingActivityTestRule = new ActivityTestRule
            <ContractBooking>(ContractBooking.class);
    private ContractBooking contractBooking = null;
    private List<View> views = new ArrayList<>();
    @Before
    public void setUp() throws Exception {
        contractBooking = contractBookingActivityTestRule.getActivity();
    }
    @Test
    public void testLaunch() {
        View view = contractBooking.findViewById(R.id.time_to_pick);       views.add(view);
        view = contractBooking.findViewById(R.id.time_to_receive);         views.add(view);
        view = contractBooking.findViewById(R.id.drop);                    views.add(view);
        view = contractBooking.findViewById(R.id.end_date);                views.add(view);
        view = contractBooking.findViewById(R.id.register);                views.add(view);
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
        contractBooking = null;
    }
}