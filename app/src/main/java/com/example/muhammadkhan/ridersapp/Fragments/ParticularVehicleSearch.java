package com.example.muhammadkhan.ridersapp.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muhammadkhan.ridersapp.Activities.Splash;
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
 * Created by Muhammad Khan on 18/04/2018.
 */

public class ParticularVehicleSearch extends android.support.v4.app.Fragment{
    private String vehicle;
    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private String driverCity=null;
    private LocationManager locationManager;
    private Location mLocation;
    private List<Driver> list=new ArrayList<Driver>();
    private ProgressDialog progressbar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view= inflater.inflate(R.layout.driver_search,container,false);
        recyclerView=(RecyclerView) view.findViewById(R.id.search_driver);
        //background thread for getting current location and data
        //getCurrentLocation();
        //background thread for Async Task
        new Background().execute();

        return view;
    }

    public void setVehicle(String vehicle){
        this.vehicle=vehicle;
    }

    public  class Background extends AsyncTask<String,List<Object>,Void> {
        SearchAdapter searchAdapter;

        @Override
        protected Void doInBackground(String... strings) {
            getVehicles(vehicle);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchAdapter=new SearchAdapter(getActivity());
            showProgressBar();
        }

        @Override
        protected void onProgressUpdate(List<Object>... values) {
            super.onProgressUpdate(values);
            searchAdapter.addList(list);
            recyclerView.setAdapter(searchAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressbar.dismiss();
        }
        public void getVehicles(String parentNode){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String currentCity = preferences.getString("current_city", "");
            reference=FirebaseDatabase.getInstance().getReference().child("Drivers").child(currentCity).child(parentNode);
            System.out.println(reference);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child:dataSnapshot.getChildren()){
                        Driver driver=child.getValue(Driver.class);
                        Log.i("model",driver.getModelName());
                        list.add(driver);
                        onProgressUpdate();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        public void showProgressBar() {
            progressbar = new ProgressDialog(getActivity());
            progressbar.setMessage("Loading data..");
            progressbar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressbar.setIndeterminate(false);
            progressbar.setCancelable(true);
            progressbar.show();
        }
    }
}
