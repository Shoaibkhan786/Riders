package com.example.muhammadkhan.ridersapp.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.muhammadkhan.ridersapp.Models.Booking;
import com.example.muhammadkhan.ridersapp.Models.Driver;
import com.example.muhammadkhan.ridersapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableList;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhammad Khan on 21/04/2018.
 */

public class BookingFragment extends Fragment{
    DatabaseReference reference;
    List<Booking> list=new ArrayList<Booking>();
    BookingAdapter adapter;
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.booking_fragment,container,false);
        recyclerView=view.findViewById(R.id.booking);
        adapter=new BookingAdapter(getActivity());
        reteriveInfo();
        return view;
    }
    public void reteriveInfo(){
        Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String currentUser= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                reference= FirebaseDatabase.getInstance().getReference("Booking").child(currentUser);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot child:dataSnapshot.getChildren()) {
                                Booking booking = child.getValue(Booking.class);
                                list.add(booking);
                                adapter.addList(list);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        MDToast mdToast = MDToast.makeText(getActivity(), databaseError.getMessage().toString(), MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                        mdToast.show();
                    }
                });
            }
        });
    }
}

