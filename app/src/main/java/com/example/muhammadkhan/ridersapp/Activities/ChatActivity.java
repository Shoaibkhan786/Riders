package com.example.muhammadkhan.ridersapp.Activities;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.Toolbar;

import com.example.muhammadkhan.ridersapp.Fragments.ChatMessageShowAdapter;
import com.example.muhammadkhan.ridersapp.Fragments.ChatMessages;
import com.example.muhammadkhan.ridersapp.Fragments.ChatMessagesAdapter;
import com.example.muhammadkhan.ridersapp.Models.Booking;
import com.example.muhammadkhan.ridersapp.Models.Chat;
import com.example.muhammadkhan.ridersapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements ChatMessagesAdapter.MessageSelect {

    private Toolbar toolbar;
    private FloatingActionButton send;
    private RecyclerView recyclerView;
    private List<Chat> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //getting data from previous activity
        Bundle bundle = getIntent().getExtras();
        Booking model = (Booking) bundle.getSerializable("object");
        list=new ArrayList<>();
        recyclerView=(RecyclerView) findViewById(R.id.chat_recycler);
        send=(FloatingActionButton) findViewById(R.id.send_btn);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_keyboard_backspace_white_24dp);
        toolbar.setTitle(model.getName());
        toolbar.setTitleTextColor(getResources().getColor(R.color.cardview_light_background));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling bottom fragment
                ChatMessages chatMessages=new ChatMessages();
                chatMessages.show(getSupportFragmentManager(),"Information");
            }
        });

    }
    @Override
    public void onSelectMessage(String text) {
        BottomSheetBehavior bottomSheetBehavior;
        Chat chat=new Chat("You",text);
        list.add(chat);
        ChatMessageShowAdapter chatMessageShowAdapter=new ChatMessageShowAdapter(getApplicationContext(),list);
        recyclerView.setAdapter(chatMessageShowAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }
}
