package com.example.muhammadkhan.ridersapp.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muhammadkhan.ridersapp.Activities.ContractBooking;
import com.example.muhammadkhan.ridersapp.Activities.DriverProfileViewForPassenger;
import com.example.muhammadkhan.ridersapp.Activities.MapsActivity;
import com.example.muhammadkhan.ridersapp.Models.Booking;
import com.example.muhammadkhan.ridersapp.Models.Driver;
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
 * Created by Muhammad Khan on 15/04/2018.
 */

public class BookingAdapter  extends RecyclerView.Adapter<BookingAdapter.MyViewHolder> {

    Context context;
    private List<Booking> list=new ArrayList<>();
    private LayoutInflater inflater;
    private ArrayAdapter<String> dataAdapter;
    public BookingAdapter(Context context) {
        this.context=context;
    }
    public void addList(List<Booking> booking) {
        this.list=booking;
    }
    @Override
    public BookingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.driver_info_booking_item,parent,false);
        MyViewHolder viewHolder=new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BookingAdapter.MyViewHolder holder, int position) {
        Booking driver=list.get(position);
        holder.setData(driver,position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CircleImageView profileImage;
        private TextView driverName;
        private TextView modelName;
        private TextView vehicleType;
        private TextView pricePerKilometer;
        private TextView pick;
        private TextView drop;
        private TextView status;
        private TextView days;
        private TextView textViewAboutDays;
        public Spinner spinner;

        public MyViewHolder(final View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.booking_image_item);
            driverName=itemView.findViewById(R.id.driver_name);
            modelName=itemView.findViewById(R.id.driver_model);
            pricePerKilometer=itemView.findViewById(R.id.price_per_kilo);
            vehicleType=itemView.findViewById(R.id.passenger_dest);
            pick=itemView.findViewById(R.id.pick);
            drop=itemView.findViewById(R.id.drop);
            status=itemView.findViewById(R.id.status);
            days=itemView.findViewById(R.id.days);
            textViewAboutDays=itemView.findViewById(R.id.days_left);
            spinner=itemView.findViewById(R.id.spinner_id);
            //spinner items
            List<String> categories = new ArrayList<String>();
            categories.add("");
            categories.add("Get Location");
            categories.add("Extend Contract");
            categories.add("Delete");
            // Creating adapter for spinner
            dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories);
            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    Booking driver=null;
                    // your code here
                    String text=parentView.getItemAtPosition(position).toString();
                    //getting clicked driver info
                   try {
                       int adapterPosition = getAdapterPosition();
                       driver = list.get(adapterPosition);
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                    if(text.equals("Get Location")){
                        //no need to show the current selected spinner items into view item.
                        if(driver.getStatus().equals("Booked")) {
                            parentView.setSelection(dataAdapter.getPosition(""));
                            Intent intent = new Intent(context, MapsActivity.class);
                            intent.putExtra("object", driver);
                            context.startActivity(intent);
                            //now we are shifted to the map activity.
                        }else {
                            parentView.setSelection(dataAdapter.getPosition(""));
                            MDToast mdToast = MDToast.makeText(context,"You can not access location at the moment.", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                    }
                    if(text.equals("Extend Contract")){
                        if(days.getText().equals("Contract Expired")){
                            //go to extend project activity
                            parentView.setSelection(dataAdapter.getPosition(""));
                            // Intent intent=new Intent(context, ContractBooking.class);
                           // int currentPosition=getAdapterPosition();
                            //Booking driverInfo=list.get(currentPosition);
                            //context.startActivity(intent);
                        }else {
                            //go to extend project activity
                            parentView.setSelection(dataAdapter.getPosition(""));
                            MDToast mdToast = MDToast.makeText(context,"You can not extend the contract right now.", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                    }
                    if(text.equals("Delete")){
                        if(driver.getStatus().equals("Booked")) {
                            parentView.setSelection(dataAdapter.getPosition(""));
                            //show alert box
                            showSettingAlert(driver);
                        }else{
                            parentView.setSelection(dataAdapter.getPosition(""));
                            // method call for deleting the contract
                            delete();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
            //spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) context);
        }
        public void setData(Booking driver,int position){
            driverName.setText(driver.getName());
            modelName.setText(driver.getModelName());
            pricePerKilometer.setText(driver.getPricePerKilometer()+" Rs.");
            vehicleType.setText(driver.getVehicleType());
            if(driver.getProfileImage().equals("No")){
                profileImage.setImageResource(R.mipmap.profile);
            }else {
                Glide.with(context).load(Uri.parse(driver.getProfileImage())).into(profileImage);
            }

            pick.setText(driver.getTimeToPick());
            drop.setText(driver.getTimeToReceive());
            status.setText(driver.getStatus());
            if(driver.getStatus().equals("Pending")){
                days.setText("No response");
                days.setTextSize(20);
                textViewAboutDays.setText("");
            }else {
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
                        textViewAboutDays.setVisibility(View.INVISIBLE);
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
            }
            //if driver reject the ride contract
            if(driver.getStatus().equals("Rejected")){
                status.setText("Rejected");
                status.setTextColor(Color.RED);
                days.setText("Rejected");
                days.setTextSize(20);
                textViewAboutDays.setVisibility(View.GONE);
            }
            if(driver.getStatus().equals("Deleted")){
                status.setText("Deleted");
                status.setTextColor(Color.RED);
                days.setText("Deleted");
                days.setTextSize(20);
                textViewAboutDays.setVisibility(View.GONE);
            }
        }
        //methoo for deleting data on firebase database
        public void delete(){
            int position=getLayoutPosition();
            list.remove(position);
            notifyItemChanged(position);

            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Booking").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            reference.removeValue();

        }
        public void showSettingAlert(final Booking driver){
            final AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("If you have booked contract with this driver,deleting it will end the contract.");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Handler handler=new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Booking").child(driver.getContact())
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

        @Override
        public void onClick(View view) {
            Log.i("click kr: ",""+view.getId());
        }
    }

}
