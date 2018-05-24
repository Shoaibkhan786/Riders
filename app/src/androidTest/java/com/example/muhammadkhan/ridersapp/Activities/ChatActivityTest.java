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

public class ChatActivityTest {
    @Rule
    public ActivityTestRule<ChatActivity> chatActivityActivityTestRule = new ActivityTestRule
            <ChatActivity>(ChatActivity.class);
    private ChatActivity chatActivity = null;
    private List<View> views = new ArrayList<>();
    @Before
    public void setUp() throws Exception {
        chatActivity = chatActivityActivityTestRule.getActivity();
    }
    @Test
    public void testLaunch() {
        View view = chatActivity.findViewById(R.id.chat_recycler);
        views.add(view);
        view = chatActivity.findViewById(R.id.send_btn);
        views.add(view);
        view = chatActivity.findViewById(R.id.toolbar);
        views.add(view);
        //method call for checking these views
        testViews(views);
    }
    @After
    public void tearDown() throws Exception {
        chatActivity = null;
    }
    //testing different views
    public void testViews(List<View> views) {
        for (int i = 0; i < views.size(); i++) {
            assertNotNull(views.get(i));
        }
    }
}