package com.example.muhammadkhan.ridersapp.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.muhammadkhan.ridersapp.Models.Booking;
import com.example.muhammadkhan.ridersapp.Models.Driver;
import com.example.muhammadkhan.ridersapp.Models.Passenger;
import com.example.muhammadkhan.ridersapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ContractBooking extends AppCompatActivity {
    DatabaseReference reference;
    private EditText timeToPick;
    private  EditText timeToReceive;
    private EditText startingDate;
    private EditText endingDate;
    private TimePickerDialog mTimePicker;
    private DatePickerDialog mDatePicker;
    private Button done;
    String currentUserPhoneNumber;
    //Model classes
    Driver model;
    Passenger passenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_booking);
        //only portrait mode
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);
        //getting data from previous activity
        Bundle bundle = getIntent().getExtras();
        model=(Driver) bundle.getSerializable("object");
        //enabling tool bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Contract Booking");
        //finding logged user
        new Thread(new Runnable() {
            @Override
            public void run() {
                //method call
                findLoggedUser();
            }
        }).start();

        //getting views id
        timeToPick=(EditText) findViewById(R.id.time_to_pick);
        timeToReceive=(EditText) findViewById(R.id.time_to_receive);
        startingDate=(EditText) findViewById(R.id.drop);
        endingDate=(EditText) findViewById(R.id.end_date);
        done=(Button) findViewById(R.id.register);
        //listener on button
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = checkUserInput();
                if (count == 0) {
                    try {
                        //getting dates
                        final Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(startingDate.getText().toString().trim());
                        final Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(endingDate.getText().toString().trim());
                        //getting time
                        String startTime = timeToPick.getText().toString().trim();
                        String endTime = timeToReceive.getText().toString().trim();
                        //if user gives correct date and time
                        if (checkDate(startDate, endDate) && checkTime(startTime, endTime)) {
                            //here the actual stuff begins
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    long days=getDays(startDate,endDate);
                                    writeInFirebaseDatabase(days);
                                }
                            }).start();
                            //Intent to Passenger activity
                            MDToast mdToast = MDToast.makeText(ContractBooking.this, "Ride request has been made.", MDToast.LENGTH_LONG, MDToast.TYPE_INFO);
                            mdToast.show();
                            Intent intent=new Intent(ContractBooking.this,PassengerMainActivity.class);
                            startActivity(intent);
                        } else {
                            MDToast mdToast = MDToast.makeText(ContractBooking.this, "Invalid Inputs", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //on click listener
        timeToPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //method call
                pickYourTime(timeToPick);
                timeToPick.setError(null);
            }
        });
        //on click listener
        startingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //method call
                pickYourDates(startingDate);
                startingDate.setError(null);
            }
        });
        //on click listener
        endingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //method call
                pickYourDates(endingDate);
                endingDate.setError(null);
            }
        });
        //on click listener
        timeToReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //method call
                pickYourTime(timeToReceive);
                timeToReceive.setError(null);
            }
        });

    }
    public long getDays(Date startDate,Date endDate){
        long diff = endDate.getTime() - startDate.getTime();
        diff=TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return diff;
    }
    //method for saving info in firebase database
    public void writeInFirebaseDatabase(long days){
        //driver will see passenger information
        Booking passengerInformation=new Booking(passenger.getName(),passenger.getProfileImage(),passenger.getLocation(),
                passenger.getCurrentAddress(),passenger.getDestinationAdress(),Long.toString(days),
                timeToPick.getText().toString().trim(),timeToReceive.getText().toString().trim(),
                currentUserPhoneNumber,"Pending",startingDate.getText().toString().trim(),endingDate.getText().toString().trim(),model.getPricePerKilometer(),null);
        reference=FirebaseDatabase.getInstance().getReference("Notification");
        reference.child(model.getDriverContact()).child(currentUserPhoneNumber).setValue(passengerInformation);
        //Passenger will see this information
        Booking driverInformation=new Booking(model.getDriverName(),model.getProfileImage(),model.getCurrentLocation()
                ,Long.toString(days), timeToPick.getText().toString().trim(),
                timeToReceive.getText().toString().trim(),model.getDriverContact(),model.getModelName(),model.getVehicleType(),
                model.getPricePerKilometer(),"Pending",startingDate.getText().toString().trim(),endingDate.getText().toString().trim());
        reference=FirebaseDatabase.getInstance().getReference("Booking");
        reference.child(currentUserPhoneNumber).child(model.getDriverContact()).setValue(driverInformation);
    }
    //method for finding current user information
    public void findLoggedUser(){
        currentUserPhoneNumber=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        reference= FirebaseDatabase.getInstance().getReference("Passengers")
                .child(model.getDriverCity().toString().trim()).child(currentUserPhoneNumber);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                passenger=dataSnapshot.getValue(Passenger.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public boolean checkTime(String startTime,String endTime){
        SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm");
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            startTime = outFormat.format(inFormat.parse(startTime));
            endTime = outFormat.format(inFormat.parse(endTime));
            Date date1 = sdf.parse(startTime);
            Date date2 = sdf.parse(endTime);
            if(date1.before(date2)) {
                return true;
            } else {
               timeToPick.setError("time is pick<time to receive.");
                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean checkDate(Date start,Date end) {
        SimpleDateFormat outFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateString = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        try {
            Date currentDateObject = outFormat.parse(currentDateString);
            if(start.equals(end)){
                return false;
            }
            if (start.before(currentDateObject) || start.after(end)) {
                startingDate.setError("Invalid date.");
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }
    //Method for date picker
    public void pickYourDates(final EditText editText){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        mDatePicker = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                }, mYear, mMonth, mDay);
        mDatePicker.show();
    }

    //Method for cotract booking
    public void pickYourTime(final EditText editText){
        Calendar mcurrentTime=Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        mTimePicker = new TimePickerDialog(ContractBooking.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                boolean isPM = (selectedHour >= 12);
                editText.setText(String.format("%02d:%02d %s", (selectedHour == 12 || selectedHour == 0) ? 12 : selectedHour % 12, selectedMinute, isPM ? "PM" : "AM"));
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time to Pick");
        mTimePicker.show();
    }
    public int checkUserInput() {
        int count=0;
        if(TextUtils.isEmpty(timeToPick.getText().toString().trim())){
            //showing  empty email message
            timeToPick.setError("The item  Name can not be empty.");
            count=1;
        }
        if(TextUtils.isEmpty(timeToReceive.getText().toString().trim())){
            //showing  empty email message
            timeToReceive.setError("The item Vehicle Type  can not be empty.");
            count=1;
        }
        if(TextUtils.isEmpty(startingDate.getText().toString().trim())){
            //showing  empty email message
            startingDate.setError("The item Vehicle Type  can not be empty.");
            count=1;
        }
        if(TextUtils.isEmpty(endingDate.getText().toString().trim())){
            //showing empty password message
            endingDate.setError("The item Space Available can not be empty.");
            count=1;
        }
        return count;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
