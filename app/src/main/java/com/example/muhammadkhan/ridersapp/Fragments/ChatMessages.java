package com.example.muhammadkhan.ridersapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muhammadkhan.ridersapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhammad Khan on 10/05/2018.
 */

    public class ChatMessages extends BottomSheetDialogFragment {
    RecyclerView recyclerView;
    String [] messages={"I am coming.","I will not come tomorrow.","On my way.","I am outside of your house.",
    "I am outside of your destination.","I am waiting for you.Hurry up bro!.","Are you coming.","Yes.","No."};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.booking_fragment,container,false);
        recyclerView=view.findViewById(R.id.booking);
        ChatMessagesAdapter adapter=new ChatMessagesAdapter(getActivity(),messages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
}
