package com.example.muhammadkhan.ridersapp.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muhammadkhan.ridersapp.Activities.LoginActivity;
import com.example.muhammadkhan.ridersapp.Models.Driver;
import com.example.muhammadkhan.ridersapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

/**
 * Created by Muhammad Khan on 15/04/2018.
 */

public class DriverSearch extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private String driverCity=null;
    private LocationManager locationManager;
    private Location mLocation;
    private List<Driver> list=new ArrayList<Driver>();
    private  ProgressDialog progressbar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view= inflater.inflate(R.layout.driver_search,container,false);
        recyclerView=(RecyclerView) view.findViewById(R.id.search_driver);
        //background thread for getting current location and data
        getCurrentLocation();

        return view;
    }
    public void getCurrentLocation() {
        locationManager=(LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER )){

            LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;
            LocationParams.Builder builder = new LocationParams.Builder()
                    .setAccuracy(trackingAccuracy);
            SmartLocation.with(getActivity()).location()
                    .oneFix()
                    .config(builder.build())
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {
                            mLocation = location;
                              Log.i("TAG",""+mLocation.getLatitude());
                             Log.i("TAG",""+mLocation.getLongitude());
                            //method call for getting city name
                            getCityName();

                        }
                    });
        }else{
            showSettingAlert();
        }
    }

    public void getCityName(){
        Geocoder geocoder = new Geocoder(getActivity(),Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(mLocation.getLatitude(),mLocation.getLongitude(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && !addresses.isEmpty()) {
            driverCity = addresses.get(0).getLocality();
            //background thread for Async Task
            new Background().execute();
            Log.i("TAG", "city name is " + driverCity);
        }
    }
    public void showSettingAlert(){
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("GPS Setting");
        alertDialog.setMessage("For better results,your current location is required! ");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        //setting negetive button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    public  class Background extends AsyncTask<Void,List<Object>,Void> {
        SearchAdapter searchAdapter;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchAdapter=new SearchAdapter(getActivity());
            //showProgressBar();
        }

        @Override
        protected Void doInBackground(Void... voids) {
             getKey();
            return null;
        }
        @Override
        protected void onProgressUpdate(List<Object>... values) {
            super.onProgressUpdate(values);
            searchAdapter.addList(list);
            recyclerView.setAdapter(searchAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        public void getKey(){
            reference=FirebaseDatabase.getInstance().getReference("Drivers").child(driverCity);
            System.out.println(reference);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child:dataSnapshot.getChildren()){
                        String parentNode=child.getKey();
                        getVehicles(parentNode);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        public void getVehicles(String parentNode){
            reference=FirebaseDatabase.getInstance().getReference("Drivers").child(driverCity).child(parentNode);
            System.out.println(reference);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child:dataSnapshot.getChildren()){
                        Driver driver=child.getValue(Driver.class);
                        Log.i("TAG","Driver name: "+driver.getDriverName());
                        Log.i("TAG","driver model: "+driver.getModelName());
                        list.add(driver);
                        onProgressUpdate();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void showProgressBar(){
            progressbar = new ProgressDialog(getActivity());
            progressbar.setMessage("wait");
            progressbar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressbar.show();
        }
    }
}
