package com.example.muhammadkhan.ridersapp.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;

import com.example.muhammadkhan.ridersapp.Fragments.BottomPopup;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.muhammadkhan.ridersapp.Models.Booking;
import com.example.muhammadkhan.ridersapp.Models.Driver;
import com.example.muhammadkhan.ridersapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // Firebase reference
    BottomPopup bottomPopup;
    DatabaseReference referenceLocationRetrieve;
    DatabaseReference referenceLocationUpload;
    //Handlers
    private Handler upload = new Handler();
    private Handler retrieve = new Handler();
    //google maps;
    private GoogleMap mMap;
    private MarkerOptions currentLocationMarker = null;
    private Marker options = null;
    //locations
    private  LocationManager locationManager;
    private android.location.LocationListener locationListener;
    //views
    Toolbar toolbar;
    //Model class
    Booking model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //only portrait mode
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_keyboard_backspace_white_24dp);
        toolbar.setTitleTextColor(getResources().getColor(R.color.cardview_light_background));
        //getting data from previous activity
        Bundle bundle = getIntent().getExtras();
        model = (Booking) bundle.getSerializable("object");
        //setting driver name on toolbar
        toolbar.setTitle(model.getName());
        //listener on toolbar icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stop background handler thread
                locationManager.removeUpdates(locationListener);
                Log.i("lala","Location has been removed");
                onBackPressed();

            }
        });
        //when user clicks on marker
        //loading map fragment
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        upload.post(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                        (MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    showSettingAlert();
                    return;
                }else {
                    locationListener=new android.location.LocationListener() {
                                @Override
                                public void onLocationChanged(final Location location) {
                                        //upload passenger location on Firebase storage
                                    referenceLocationUpload = FirebaseDatabase.getInstance().getReference("Booking").
                                            child(model.getContact().toString()).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
                                            .toString()).child("currentLocation");
                                    referenceLocationUpload.setValue(location.getLatitude() + " " + location.getLongitude());
                                    Log.i("kaka","upload kr rha hn");
                                }
                                @Override
                                public void onStatusChanged(String s, int i, Bundle bundle) {
                                }
                                @Override
                                public void onProviderEnabled(String s) {
                                }
                                @Override
                                public void onProviderDisabled(String s) {
                                    }
                                };
                    //getting location services
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,locationListener);
                }
            }
                //end of handler runnable
        });
        //end of onCreate method
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            //setting click even on map marker
            mMap.setOnMarkerClickListener(this);
            //Current location of the driver or passenger is fetched from firebase storage
            retrieve.post(new Runnable() {
                @Override
                public void run() {
                    referenceLocationRetrieve = FirebaseDatabase.getInstance().getReference("Booking")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                            .child(model.getContact())
                            .child("currentLocation");
                    referenceLocationRetrieve.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String latLng = dataSnapshot.getValue().toString().trim();
                            Log.i("kaka","reterive kr rha hn");
                            //show driver or passenger position on Map
                            showDriverOrPassengerOnMap(latLng);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }else {
            showSettingAlert();
        }
    }
    public void showDriverOrPassengerOnMap(String latLng){
        //removing marker
        if(options!=null){
            options.remove();
        }
        //getting key to figure out the user type(i.e driver or passenger)
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
        String key = preferences.getString("key", "");
        String[] location=latLng.split(" ");

        //if  user is passenger
        if(key.equals("AS PASSENGER")){
            LatLng driverLocation = new LatLng(Float.parseFloat(location[0]),Float.parseFloat(location[1]));
            updateMarker(driverLocation,model.getVehicleType().toString().trim());
        }
        //if user is driver
        if(key.equals("AS Driver")){
            LatLng passengerLocation = new LatLng(Float.parseFloat(location[0]),Float.parseFloat(location[1]));
            currentLocationMarker = new MarkerOptions().position(passengerLocation).title(model.getName());
            currentLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_person_black_24dp));
            options = mMap.addMarker(currentLocationMarker);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(passengerLocation)
                    .zoom(23).tilt(45).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
    public void updateMarker(LatLng latLng,String vehicleType){
        if(vehicleType.equals("Van")) {
            currentLocationMarker = new MarkerOptions().position(latLng).title(model.getName());
            currentLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.van));
            options = mMap.addMarker(currentLocationMarker);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                    .zoom(23).tilt(45).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }else if(vehicleType.equals("Car")){
            currentLocationMarker = new MarkerOptions().position(latLng).title(model.getName());
            currentLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_local_taxi_black_24dp));
            options = mMap.addMarker(currentLocationMarker);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                    .zoom(23).tilt(45).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }else if(vehicleType.equals("MotorBike")){
            currentLocationMarker = new MarkerOptions().position(latLng).title(model.getName());
            currentLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_motorcycle_black_24dp));
            options = mMap.addMarker(currentLocationMarker);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                    .zoom(23).tilt(45).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }else if(vehicleType.equals("Bus")){
            currentLocationMarker = new MarkerOptions().position(latLng).title(model.getName());
            currentLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_directions_bus_black_24dp));
            options = mMap.addMarker(currentLocationMarker);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                    .zoom(23).tilt(45).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }else if(vehicleType.equals("Rikhshaw")){
            currentLocationMarker = new MarkerOptions().position(latLng).title(model.getName());
            currentLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.rikshaw));
            options = mMap.addMarker(currentLocationMarker);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                    .zoom(23).tilt(45).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }else if(vehicleType.equals("QINGQI")){
            currentLocationMarker = new MarkerOptions().position(latLng).title(model.getName());
            currentLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.qinjqi));
            options = mMap.addMarker(currentLocationMarker);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                    .zoom(23).tilt(45).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }else {
            currentLocationMarker = new MarkerOptions().position(latLng).title(model.getName());
            currentLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.bolan_sazuki));
            options = mMap.addMarker(currentLocationMarker);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                    .zoom(23).tilt(45).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void showSettingAlert(){
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS Setting");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        //setting positive button
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("calling",""+getCallingActivity());
        bottomPopup=new BottomPopup(getApplicationContext(),model);
        bottomPopup.show(getSupportFragmentManager(),"Information");
        return true;
    }
}