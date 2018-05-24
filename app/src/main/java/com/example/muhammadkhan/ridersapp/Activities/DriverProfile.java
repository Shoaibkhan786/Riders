package com.example.muhammadkhan.ridersapp.Activities;
import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.example.muhammadkhan.ridersapp.Models.Driver;
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
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DriverProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //location related stuff
    LocationManager locationManager=null;
    Location mLocation=null;
    String driverCity;
    double lat;
    double longi;
    String [] imageUrl=new String[5];
    //Firebase stuff
    UploadTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageRef,imageRef;

    private static final int RESULT_LOAD_IMAGE=15;
    Uri userProfileUri=null;
    //Edit texts
    String vehicle;
    private EditText modelName;
    private EditText driverName;
    private EditText totalCapcity;
    private EditText space;
    private EditText pricePerKilometer;
    //image views
    private ImageView driverImage;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private Spinner spinner;
    private Button register;
    private ProgressDialog progressBar=null;
    //toast message
    MDToast mdToast;
    private ArrayList<String> pickPhoto=new ArrayList<>();
    Driver driverInfo=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registeration);
        //method call for getting user current location and city name
        Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //method call
                getCurrentLocation();
            }
        });
        //method for saving device token into MySql with the help of php local server
         new SaveUsersDeviceToken().execute();

       // getting Image views references
        imageView1=(ImageView) findViewById(R.id.first_image);
        imageView2=(ImageView) findViewById(R.id.second_image);
        imageView3=(ImageView) findViewById(R.id.third_image);
        imageView4=(ImageView) findViewById(R.id.fourth_image);
        driverImage=(ImageView) findViewById(R.id.image);

        //getting edit text reference
        driverName=(EditText) findViewById(R.id.driver_name);
        space=(EditText) findViewById(R.id.space);
        pricePerKilometer=(EditText) findViewById(R.id.price_per_kilo);
        totalCapcity=(EditText) findViewById(R.id.total_capacity);
        modelName=(EditText) findViewById(R.id.model_name);

        register=(Button) findViewById(R.id.register);
        spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("MotorBike");
        categories.add("Car");
        categories.add("SazukiBolan");
        categories.add("Van");
        categories.add("Bus");
        categories.add("QINGQI");
        categories.add("Rikshaw");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);

        //driver image on click listener
        driverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageFromGallery();
            }
        });
        driverName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                driverName.setError(null);
            }
        });
        modelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modelName.setError(null);
            }
        });
        totalCapcity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                totalCapcity.setError(null);
            }
        });
        space.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                space.setError(null);
            }
        });
        pricePerKilometer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pricePerKilometer.setError(null);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> imageList = new ArrayList<String>(Arrays.asList(imageUrl));
                if(checkUserInput()==0 && pickPhoto.size()==4){
                    //saving vehicle name and driver city
                    SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(DriverProfile.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("vehicle",vehicle);
                    editor.putString("currentCity",driverCity);
                    editor.apply();

                    if(userProfileUri==null) {
                        driverInfo = new Driver(driverName.getText().toString().trim(),"No",modelName.getText().toString().trim(),totalCapcity.getText().toString().trim(),
                                space.getText().toString().trim(), pricePerKilometer.getText().toString().trim(),
                                vehicle,driverCity,FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),
                                String.valueOf(mLocation.getLatitude())+ " " +String.valueOf(mLocation.getLongitude()));
                        //method call
                        writeIntoDatabase(driverInfo);
                    }else{
                            driverInfo = new Driver(driverName.getText().toString().trim(),userProfileUri.toString(),modelName.getText().toString().trim(),totalCapcity.getText().toString().trim(),
                                    space.getText().toString().trim(), pricePerKilometer.getText().toString().trim(),
                                    vehicle,driverCity,FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),
                                    String.valueOf(mLocation.getLatitude())+ " " +String.valueOf(mLocation.getLongitude()));
                        //method call
                        writeIntoDatabase(driverInfo);
                    }

                }else {
                   mdToast = MDToast.makeText(DriverProfile.this, "Please fill all fields properly.", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                    mdToast.show();
                }
            }
        });
    }

    public void uploadImageFromGallery() {
        //image gallery intent
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                if(resultCode== Activity.RESULT_OK && data!=null) {
                    if(requestCode==RESULT_LOAD_IMAGE){
                        userProfileUri=data.getData();
                        //method call
                        uploadProfileImage();
                        Glide.with(this).load(userProfileUri).into(driverImage);
                        return;
                    }
                    pickPhoto.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    if(pickPhoto.size()==4) {
                        Glide.with(this).load(Uri.fromFile(new File(pickPhoto.get(0)))).into(imageView1);
                        Glide.with(this).load(Uri.fromFile(new File(pickPhoto.get(1)))).into(imageView2);
                        Glide.with(this).load(Uri.fromFile(new File(pickPhoto.get(2)))).into(imageView3);
                        Glide.with(this).load(Uri.fromFile(new File(pickPhoto.get(3)))).into(imageView4);
                        //getting images imageUri
                        imageUrl[0]=Uri.parse(pickPhoto.get(0)).toString();
                        imageUrl[1]=Uri.parse(pickPhoto.get(1)).toString();
                        imageUrl[2]=Uri.parse(pickPhoto.get(2)).toString();
                        imageUrl[3]=Uri.parse(pickPhoto.get(3)).toString();
                        //background thread for image uploading
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //method call
                                ImageUpload imageUpload=new ImageUpload(DriverProfile.this);
                                imageUpload.uploadImageOnServer(pickPhoto);
                            }
                        }).start();
                    }else{
                        mdToast = MDToast.makeText(this, "At least 4 picture can be uploaded", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                        mdToast.show();
                    }
                }
    }
    public void uploadProfileImage(){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imageRef = storageRef.child("Driver_Vehicles").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                child("profile_Image");
        //image uploading
        uploadTask=imageRef.putFile(userProfileUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                userProfileUri=taskSnapshot.getDownloadUrl();
            }});

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mdToast = MDToast.makeText(DriverProfile.this, "Sorry,could not upload pictures", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                mdToast.show();
            }
        });


    }
    public void pickVehiclePhoto(View view){
        pickPhoto.clear();
        FilePickerBuilder.getInstance().setMaxCount(4)
                .setSelectedFiles(pickPhoto)
                .pickPhoto(DriverProfile.this);
    }
    public int checkUserInput() {
        int count=0;
        if(TextUtils.isEmpty(driverName.getText().toString().trim())){
            //showing  empty email message
            driverName.setError("The item  Name can not be empty.");
            count=1;
        }
        if(TextUtils.isEmpty(totalCapcity.getText().toString().trim())){
            //showing  empty email message
            totalCapcity.setError("The item Vehicle Type  can not be empty.");
            count=1;
        }
        if(TextUtils.isEmpty(modelName.getText().toString().trim())){
            //showing  empty email message
            modelName.setError("The item Vehicle Type  can not be empty.");
            count=1;
        }
        if(TextUtils.isEmpty(space.getText().toString().trim())){
            //showing empty password message
            space.setError("The item Space Available can not be empty.");
            count=1;
        }
        if(TextUtils.isEmpty(pricePerKilometer.getText().toString().trim())){
            //showing empty password message
            pricePerKilometer.setError("The item Price/km can not be empty");
            count=1;
        }
        return count;
    }
    public void writeIntoDatabase(Driver driver){
        String currentUser=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Drivers").child(driverCity).child(vehicle).
                child(currentUser);
        reference.setValue(driver);
        // intent to main activity
        Intent intent=new Intent(DriverProfile.this,DriverMainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        vehicle=adapterView.getItemAtPosition(i).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void getCurrentLocation() {
        locationManager=(LocationManager)this.getSystemService(this.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER )){
            LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;
            LocationParams.Builder builder = new LocationParams.Builder()
                    .setAccuracy(trackingAccuracy);
            SmartLocation.with(this).location()
                    .oneFix()
                    .config(builder.build())
                    .start(new OnLocationUpdatedListener() {

                        @Override
                        public void onLocationUpdated(Location location) {
                            mLocation = location;
                          //  Log.i("TAG",""+mLocation.getLatitude());
                           // Log.i("TAG",""+mLocation.getLongitude());
                            //method call for getting city name
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
            driverCity= addresses.get(0).getLocality();
            Log.i("TAG", driverCity);
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
    // method for saving device token into MySql
    public void saveDeviceToken(){
    //Background thread to do this task
        Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("userContact",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                            .add("token", FirebaseInstanceId.getInstance().getToken())
                            .build();
                    Request request = new okhttp3.Request.Builder()
                            .url("http://192.168.1.195/register.php")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();

                    Log.i("Tag", "doInBackground: "+response.message());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //saving user device token
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

