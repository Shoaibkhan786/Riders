package com.example.muhammadkhan.ridersapp.Activities;

import android.app.Activity;
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
public class SplashScreenTest {
    @Rule
    public ActivityTestRule<Splash> splashActivity=new ActivityTestRule<Splash>(Splash.class);
    private Splash splash=null;
    private List<View> views=new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        splash=splashActivity.getActivity();
    }
    @Test
    public void testLaunch(){
        View view=splash.findViewById(R.id.riders);
        views.add(view);
        view=splash.findViewById(R.id.weclome);
        views.add(view);
        testViews(views);
    }
    @After
    public void tearDown() throws Exception {
    splash=null;
    }
    //testing different views
    public void testViews(List<View> views){
        for(int i=0;i<views.size();i++){
            assertNotNull(views.get(i));
        }
    }

}