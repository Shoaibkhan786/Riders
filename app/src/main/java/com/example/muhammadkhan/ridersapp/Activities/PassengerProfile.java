package com.example.muhammadkhan.ridersapp.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muhammadkhan.ridersapp.Models.Passenger;
import com.example.muhammadkhan.ridersapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PassengerProfile extends AppCompatActivity {
    final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    Location mLocation;
    LocationManager locationManager=null;

    ProgressDialog progressDialog = null;
    //Firebase
    private FirebaseAuth auth;
    private DatabaseReference firebaseDatabase;
    private FirebaseStorage storage;
    private StorageReference storageRef, imageRef;
    private UploadTask uploadTask;
    private static final int RESULT_LOAD_IMAGE = 1;
    private Uri imageUri = null;
    //views
    private EditText userName;
    private EditText currentAddress;
    private EditText destinationAddress;
    private TextView contact;
    String city;
    private CircleImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_profile);

        //method for saving device token into MySql with the help of php local server
        new SaveUsersDeviceToken().execute();

        //method call for gps permission
        permissionCheck();

        userName = (EditText) findViewById(R.id.passenger_name);
        currentAddress = (EditText) findViewById(R.id.current_address);
        destinationAddress = (EditText) findViewById(R.id.dest_address);
        contact = (TextView) findViewById(R.id.contact_info);
        imageView = (CircleImageView) findViewById(R.id.passenger_image);
        //setting user contact number
        contact.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        //getting current Location

        //profile image listener
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageFromGallery();
            }
        });
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                userName.setError(null);
            }
        });
        currentAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                currentAddress.setError(null);
            }
        });
        destinationAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                destinationAddress.setError(null);
            }
        });
    }

    //-------------------method for saving passenger data on firebase---------------------------------------------//
    public void saveChanges(View view) {
        if (checkUserInputs() == 0) {
            if (imageUri == null) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Wait");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                //method call
                putInDatabase("null");
                progressDialog.dismiss();
            } else {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Wait");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                //method call
                uploadImageOnServer();
            }
        }
    }

    //-------------------method for checking user inputs----------------------------------------------------------//
    public int checkUserInputs() {
        int count = 0;
        if (TextUtils.isEmpty(userName.getText().toString().trim())) {
            //showing  empty email message
            userName.setError("The item  Name can not be empty.");
            count = 1;
        }
        if (TextUtils.isEmpty(currentAddress.getText().toString().trim())) {
            //showing  empty email message
            currentAddress.setError("The item current address can not be empty.");
            count = 1;
        }
        if (TextUtils.isEmpty(destinationAddress.getText().toString().trim())) {
            //showing empty password message
            destinationAddress.setError("The item destination address can not be empty.");
            count = 1;
        }

        return count;
    }

    //-------------------------------method for taking image from gallery-------------------------------------------//
    public void uploadImageFromGallery() {
        //image gallery intent
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    //-------------------------------------------------call back Method--------------------------------------------------------//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri =data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    //----------------------------------put all information in database-------------------------------------------------------//
    public void putInDatabase(String imageUri) {
        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        //Model class Passenger
        Passenger passenger = new Passenger(userName.getText().toString().trim(), currentAddress.getText().toString().trim(),
                destinationAddress.getText().toString().trim(),phoneNumber,
                city, String.valueOf(mLocation.getLatitude())+ " " +String.valueOf(mLocation.getLongitude()) , imageUri.toString());
        //firebase database
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Passengers");
        firebaseDatabase.child(city).child(phoneNumber).setValue(passenger);
        //dismiss progress bar
        progressDialog.dismiss();
        //method call for showing user main screen activity
        mainActivityIntent();
    }

    //-------------------------------------main activity intent--------------------------------------------------//
    public void mainActivityIntent() {
        Intent intent = new Intent(PassengerProfile.this, PassengerMainActivity.class);
        startActivity(intent);
    }

    //-------------------------------------------Method for uploading user picture on firebase server----------------------------------//
    public void uploadImageOnServer() {
        //image uploading
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imageRef = storageRef.child("Passengers").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(1));
        uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                imageUri = taskSnapshot.getDownloadUrl();
                                                //method call for database
                                                putInDatabase(imageUri.toString());
                                            }
                                        }
        );
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PassengerProfile.this, "Sorry could not upload the image", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void permissionCheck() {
        locationManager=(LocationManager)this.getSystemService(this.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER )){
            SmartLocation.with(this).location()
                    .oneFix()
                    .start(new OnLocationUpdatedListener() {

                        @Override
                        public void onLocationUpdated(Location location) {
                            mLocation = location;
                            getCityName();
                        }
                    });
        }else{
            showSettingAlert();
        }
    }
    public void getCityName(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        Log.i("TAG", ""+mLocation.getLongitude());
        Log.i("TAG",""+mLocation.getLatitude());
        try {
            addresses = geocoder.getFromLocation(mLocation.getLatitude(),mLocation.getLongitude(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && !addresses.isEmpty()) {
            city = addresses.get(0).getLocality();
            //saving vehicle name and driver city
            SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(PassengerProfile.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("passenger_city",city);
            editor.apply();
            Log.i("TAG", city);
        }
    }
    public void showSettingAlert(){
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS Setting");
        alertDialog.setMessage("Current location is required for sign up! ");

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

    public class SaveUsersDeviceToken extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("userContact",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                        .add("token",FirebaseInstanceId.getInstance().getToken())
                        .build();
                Request request = new okhttp3.Request.Builder()
                        .url("http://172.19.0.150/register.php")
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                Log.i("TAG", "doInBackground: "+response.message());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}