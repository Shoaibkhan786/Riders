package com.example.muhammadkhan.ridersapp.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muhammadkhan.ridersapp.Activities.MapsActivity;
import com.example.muhammadkhan.ridersapp.Models.Booking;
import com.example.muhammadkhan.ridersapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Muhammad Khan on 23/04/2018.
 */

public class DriverBookingAdpater extends RecyclerView.Adapter<DriverBookingAdpater.MyViewHolder>  {
    Context context;
    private List<Booking> list;
    private LayoutInflater inflater;
    //spinner adapter
    private ArrayAdapter<String> dataAdapter;

    public DriverBookingAdpater(Context context) {
        this.context=context;
    }
    public void addList(List<Booking> booking) {
        this.list=booking;
    }
    @Override
    public DriverBookingAdpater.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.passenger_info_booking_item,parent,false);
        DriverBookingAdpater.MyViewHolder viewHolder=new DriverBookingAdpater.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DriverBookingAdpater.MyViewHolder holder, int position) {
        Booking driver=list.get(position);
        holder.setData(driver);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CircleImageView profileImage;
        private TextView passengerName;
        private TextView passengerCurrentAddress;
        private TextView passengerDestinationAddress;
        private TextView pricePerKilometer;
        private TextView pick;
        private TextView drop;
        private TextView status;
        private TextView days;
        private TextView daysLeft;
        private Spinner spinner;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            passengerName=itemView.findViewById(R.id.driver_name);
            passengerCurrentAddress=itemView.findViewById(R.id.driver_model);
            profileImage=itemView.findViewById(R.id.booking_image_item);
            passengerDestinationAddress=itemView.findViewById(R.id.passenger_dest);
            pricePerKilometer=itemView.findViewById(R.id.price_per_kilo);
            pick=itemView.findViewById(R.id.pick);
            drop=itemView.findViewById(R.id.drop);
            status=itemView.findViewById(R.id.status);
            days=itemView.findViewById(R.id.days);
            daysLeft=itemView.findViewById(R.id.remaining_days);
            spinner=itemView.findViewById(R.id.spinner_passenger);
            //spinner items
            List<String> categories = new ArrayList<String>();
            categories.add("");
            categories.add("Get Location");
            categories.add("Delete");
            // Creating adapter for spinner
            dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories);
            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    //getting clicked driver info
                    Booking passenger=null;
                    try {
                        int adapterPosition=getAdapterPosition();
                        passenger=list.get(adapterPosition);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    String text=parentView.getItemAtPosition(position).toString();
                    if(text.equals("Get Location")){
                        if(passenger.getStatus().equals("Booked")) {
                            //no need to show the current selected spinner items into view item.
                            parentView.setSelection(dataAdapter.getPosition(""));
                            Intent intent = new Intent(context, MapsActivity.class);
                            intent.putExtra("object", passenger);
                            context.startActivity(intent);
                        }else {
                            parentView.setSelection(dataAdapter.getPosition(""));
                            MDToast mdToast = MDToast.makeText(context,"You can not access location at the moment.", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                    }
                    if(text.equals("Delete")){
                        //delete the contract
                        parentView.setSelection(dataAdapter.getPosition(""));
                        if(passenger.getStatus().equals("Booked")){
                           showSettingAlert(passenger);
                        }else {
                            //delete the contract
                            parentView.setSelection(dataAdapter.getPosition(""));
                            delete();
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
        }
        public void setData(Booking driver){
            passengerName.setText(driver.getName());
            passengerCurrentAddress.setText(driver.getCurrentAddress());
            passengerDestinationAddress.setText(driver.getDestinationAddress());
            pricePerKilometer.setText(driver.getPricePerKilometer()+" Rs.");
            if(driver.getProfileImage().equals("null")||driver.getProfileImage().equals("No")){
                profileImage.setImageResource(R.mipmap.profile);
            }else {
                Glide.with(context).load(Uri.parse(driver.getProfileImage())).into(profileImage);
            }
            pick.setText(driver.getTimeToPick());
            drop.setText(driver.getTimeToReceive());
                try {
                    long remainingDays;
                    SimpleDateFormat outFormat = new SimpleDateFormat("dd/MM/yyyy");
                    //getting current date
                    String currentDateString = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                    Date currentDateObject = outFormat.parse(currentDateString);
                    //getting starting and ending date
                    String endingDate = driver.getEndingDate();
                    String startingDate=driver.getStartingDate();
                    //making starting date and ending date object
                    Date startingDateObject=outFormat.parse(startingDate);
                    Date endingDateObject = outFormat.parse(endingDate);
                    //if current date is less than the starting date
                    if(currentDateObject.before(startingDateObject)){
                        remainingDays = endingDateObject.getTime() - startingDateObject.getTime();
                        remainingDays = TimeUnit.DAYS.convert(remainingDays, TimeUnit.MILLISECONDS);
                        days.setText(String.valueOf(remainingDays));
                    }
                    else if(currentDateObject.after(endingDateObject)){
                        days.setText("Contract Expired");
                        days.setTextSize(20);
                        daysLeft.setText("");
                    }else {
                        remainingDays = endingDateObject.getTime() - currentDateObject.getTime();
                        remainingDays = TimeUnit.DAYS.convert(remainingDays, TimeUnit.MILLISECONDS)+1;
                        days.setText(String.valueOf(remainingDays));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                status.setText(driver.getStatus());
                status.setTextColor(Color.parseColor("#ff669900"));

            if(driver.getStatus().equals("Deleted")){
                status.setText("Deleted");
                status.setTextColor(Color.RED);
                days.setText("Deleted");
                days.setTextSize(20);
                daysLeft.setVisibility(View.GONE);
            }
            }
        @Override
        public void onClick(View view) {

        }
        //methoo for deleting data on firebase database
        public void delete(){
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Booking").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            reference.removeValue();
            int position=getAdapterPosition();
            list.remove(position);
            notifyItemChanged(position);
        }
        public void showSettingAlert(final Booking passenger){
            final AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("If you have booked contract with this passenger,deleting it will end the contract.");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Handler handler=new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Booking").child(passenger.getContact())
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("status");
                            reference.setValue("Deleted");
                            delete();

                        }
                    });
                }
            });
            //setting negetive button
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            alertDialog.show();
        }
    }


}

