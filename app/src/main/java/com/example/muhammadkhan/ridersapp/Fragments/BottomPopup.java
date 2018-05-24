package com.example.muhammadkhan.ridersapp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muhammadkhan.ridersapp.Activities.ChatActivity;
import com.example.muhammadkhan.ridersapp.Activities.MapsActivity;
import com.example.muhammadkhan.ridersapp.Models.Booking;
import com.example.muhammadkhan.ridersapp.Models.Chat;
import com.example.muhammadkhan.ridersapp.R;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.List;

/**
 * Created by Muhammad Khan on 09/05/2018.
 */

public class BottomPopup extends BottomSheetDialogFragment {
    private static final int REQUEST_PHONE_CALL = 1;
    private Context context;
    //model class reference
    private Booking booking;
    //views
    private ImageView profile;
    private TextView name;
    private FloatingActionButton call;
    private FloatingActionButton chat;

    public BottomPopup(Context context, Booking data) {
        this.context = context;
        this.booking = data;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.bottom_popup, container, false);
        //getting views references
        profile = view.findViewById(R.id.image);
        name = view.findViewById(R.id.name);
        call = view.findViewById(R.id.call);
        chat = view.findViewById(R.id.chat);
        //if user did not upload  the image
        if (booking.getProfileImage().equals("No") || booking.getProfileImage().equals("null")) {
            profile.setImageResource(R.mipmap.profile);
        } else {
            Glide.with(context).load(Uri.parse(booking.getProfileImage())).into(profile);
        }
        name.setText(booking.getName());
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dismiss();
                //Intent to the chat activity
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("object", booking);
                startActivity(intent);
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                    return;
                }else {
                    //making a phone call to a driver or passenger
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+booking.getContact().trim()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
        });
        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //making a phone call to a driver or passenger
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+booking.getContact().toString().trim()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else
                {
                    MDToast mdToast = MDToast.makeText(context,"No call permission", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                    mdToast.show();
                }
                return;
            }
        }
    }
}
