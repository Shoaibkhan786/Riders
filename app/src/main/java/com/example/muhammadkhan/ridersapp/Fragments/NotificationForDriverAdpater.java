package com.example.muhammadkhan.ridersapp.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muhammadkhan.ridersapp.Activities.DriverMainActivity;
import com.example.muhammadkhan.ridersapp.Activities.DriverProfile;
import com.example.muhammadkhan.ridersapp.AlramBroadcaster;
import com.example.muhammadkhan.ridersapp.Models.Booking;
import com.example.muhammadkhan.ridersapp.R;
import com.example.muhammadkhan.ridersapp.SendingNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Muhammad Khan on 22/04/2018.
 */

public class NotificationForDriverAdpater extends RecyclerView.Adapter<NotificationForDriverAdpater.MyViewHolder> {

    private Context context;
    private List<Booking> list;
    private LayoutInflater inflater;


    public NotificationForDriverAdpater(Context context){
        this.context=context;
    }
    public void addList(List<Booking> list){
        this.list=list;
    }

    @Override
    public NotificationForDriverAdpater.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.notification_item,parent,false);
        NotificationForDriverAdpater.MyViewHolder viewHolder=new NotificationForDriverAdpater.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotificationForDriverAdpater.MyViewHolder holder, int position) {
        Booking driver=list.get(position);
        holder.addData(driver);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView passengerName;
        private ImageView passengerImages;
        private TextView currentAddress;
        private TextView destinationAddress;
        private TextView pick;
        private TextView drop;
        private TextView contractStart;
        private TextView contractEnd;
        private Button accept;
        private Button reject;

        public MyViewHolder(View itemView) {
            super(itemView);
            passengerName=itemView.findViewById(R.id.passenger_name);
            passengerImages=itemView.findViewById(R.id.passenger_image);
            currentAddress=itemView.findViewById(R.id.passenger_current_address);
            destinationAddress=itemView.findViewById(R.id.passenger_destination);
            pick=itemView.findViewById(R.id.pick);
            drop=itemView.findViewById(R.id.drop);
            contractStart=itemView.findViewById(R.id.contract_start);
            contractEnd=itemView.findViewById(R.id.contract_end);
            accept=itemView.findViewById(R.id.accept);
            reject=itemView.findViewById(R.id.reject);
            //listener on button
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    Booking bookingObject = (Booking) list.get(position);
                   // bookingObject.setStatus("Booked");
                    final String passengerContact = bookingObject.getContact();
                    //Changing Passenger node
                    DatabaseReference passengerNodeReference = FirebaseDatabase.getInstance().getReference("Booking");
                    passengerNodeReference.child(passengerContact)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("status").setValue("Booked");

                    //shifting node from Notification to booked
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notification").
                            child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(passengerContact);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Booking booking = dataSnapshot.getValue(Booking.class);
                            booking.setStatus("Booked");
                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Booking");
                            reference2.child(FirebaseAuth.getInstance().
                                    getCurrentUser().getPhoneNumber()).child(passengerContact).setValue(booking);
                            //removing notification
                            reference2 = FirebaseDatabase.getInstance().getReference("Notification");
                            reference2.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).removeValue();
                            list.remove(position);
                            notifyItemChanged(position);
                            MDToast mdToast = MDToast.makeText(context, "Contract is booked now", MDToast.LENGTH_LONG, MDToast.TYPE_INFO);
                            mdToast.show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //send the notification to passegner
                    new SendingNotification().execute(passengerContact,"Congrats!Your ride contract is accepted by driver");
                    //shift the driver to the main activity
                    Intent intent1=new Intent(context, DriverMainActivity.class);
                    context.startActivity(intent1);
                }

            });
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //we will do something here
                    int position=getAdapterPosition();
                    Booking booking= (Booking) list.get(position);
                    final String passengerContact=booking.getContact();
                    Handler handler=new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Changing Passenger node
                            DatabaseReference passengerNodeReference = FirebaseDatabase.getInstance().getReference("Booking").child(passengerContact)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("status");
                            passengerNodeReference.setValue("Rejected");
                            //removing driver notification
                            passengerNodeReference = FirebaseDatabase.getInstance().getReference("Notification").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                            passengerNodeReference.removeValue();
                        }
                    });
                    list.remove(position);
                    notifyItemChanged(position);
                    MDToast mdToast = MDToast.makeText(context,"Ride request has been rejected", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                    mdToast.show();
                    //send notfication to passenger
                    //send the notification to passegner
                    new SendingNotification().execute(passengerContact,"Sorry!Your contract is rejected by driver");
                    //shift the driver to the main activity
                    Intent intent=new Intent(context, DriverMainActivity.class);
                    context.startActivity(intent);
                }
            });
        }
        public void addData(final Booking booking){
            this.passengerName.setText(booking.getName());
            if(booking.getProfileImage().equals("null")){
                passengerImages.setImageResource(R.mipmap.profile);
            }else {
                Glide.with(context).load(Uri.parse(booking.getProfileImage())).into(passengerImages);
            }
            currentAddress.setText(booking.getCurrentAddress());
            destinationAddress.setText(booking.getDestinationAddress());
            pick.setText(booking.getTimeToPick());
            drop.setText(booking.getTimeToReceive());
            contractStart.setText(booking.getStartingDate());
            contractEnd.setText(booking.getEndingDate());
        }
        @Override
        public void onClick(View view) {

        }
    }
}
