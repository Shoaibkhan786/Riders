package com.example.muhammadkhan.ridersapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muhammadkhan.ridersapp.Models.Driver;
import com.example.muhammadkhan.ridersapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DriverProfileViewForPassenger extends AppCompatActivity {
    private EditText modelName;
    private EditText driverName;
    private EditText totalCapcity;
    private EditText space;
    private EditText pricePerKilometer;
    //layout reference
    private LinearLayout linear1;
    private LinearLayout linear2;
    //image views
    private ImageView driverImage;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private Button bookIt;
    //text views
    private Spinner spinner;
    private TextView vehicleType;
    private TextView imageText;
    //model class reference
    private Driver model;
    //Firebase
    StorageReference storageRef;
    FirebaseStorage storage;
    //List<Uri> list=new ArrayList<>(4);
    String[] imageUri =new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registeration);
        // getting Image views references
        imageView1 = (ImageView) findViewById(R.id.first_image);
        imageView2 = (ImageView) findViewById(R.id.second_image);
        imageView3 = (ImageView) findViewById(R.id.third_image);
        imageView4 = (ImageView) findViewById(R.id.fourth_image);
        driverImage = (ImageView) findViewById(R.id.image);
        //getting edit text reference
        driverName = (EditText) findViewById(R.id.driver_name);
        driverName.setFocusable(false);
        space = (EditText) findViewById(R.id.space);
        space.setFocusable(false);
        pricePerKilometer = (EditText) findViewById(R.id.price_per_kilo);
        pricePerKilometer.setFocusable(false);
        totalCapcity = (EditText) findViewById(R.id.total_capacity);
        totalCapcity.setFocusable(false);
        modelName = (EditText) findViewById(R.id.model_name);
        modelName.setFocusable(false);
        bookIt = (Button) findViewById(R.id.register);
        //make these views kick off
        imageText=(TextView) findViewById(R.id.images_text);
        imageText.setVisibility(View.GONE);
        spinner=(Spinner) findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);
        vehicleType=(TextView)findViewById(R.id.vehicle_texview);
        vehicleType.setVisibility(View.GONE);
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //getting linear layouts
        linear1=(LinearLayout) findViewById(R.id.linearLayout1);
        linear2=(LinearLayout) findViewById(R.id.linearLayout2);
        //listener on above layouts
        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DriverProfileViewForPassenger.this,FullscreenActivityForImage.class);
                intent.putExtra("images",imageUri);
                intent.putExtra("object",model);
                startActivity(intent);
            }
        });
        linear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DriverProfileViewForPassenger.this,FullscreenActivityForImage.class);
                intent.putExtra("images",imageUri);
                intent.putExtra("object",model);
                startActivity(intent);
            }
        });
        bookIt.setText("Book It");
        //listener on book it button
        bookIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DriverProfileViewForPassenger.this,ContractBooking.class);
                intent.putExtra("object",model);
                startActivity(intent);
            }
        });
        //receive object in intent
        model = (Driver) getIntent().getSerializableExtra("object");
       Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                retrieveDriverInformation();
            }
        });

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
    public void retrieveDriverInformation() {
        if (model.getProfileImage().equals("No")) {
            driverImage.setImageResource(R.mipmap.profile);
        } else {
            Glide.with(this).load(Uri.parse(model.getProfileImage())).into(driverImage);
        }
        driverName.setText(model.getDriverName());
        modelName.setText(model.getModelName());
        totalCapcity.setText(model.getTotalCapacity());
        space.setText(model.getSpace());
        pricePerKilometer.setText(model.getPricePerKilometer());
        String contact = model.getDriverContact();

        AsyncTask bak = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
            }
        };
        bak.execute();
        //driver vehicle images
        storage = FirebaseStorage.getInstance();
        for (int i = 0; i < 4; i++) {
            storageRef = storage.getReference("Driver_Vehicles/" + contact + "/" + String.valueOf(i));
            //Log.i("TAG",""+storageRef);
            final int finalI = i;
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    int index = finalI;
                    imageUri[index]=uri.toString();
                    if (index == 0) {
                        Glide.with(DriverProfileViewForPassenger.this).load(uri).into(imageView1);
                    }
                    if (index == 1) {
                        Glide.with(DriverProfileViewForPassenger.this).load(uri).into(imageView2);
                        return;
                    }
                    if (index == 2) {
                        Glide.with(DriverProfileViewForPassenger.this).load(uri).into(imageView3);
                    }
                    if (index == 3) {
                        Glide.with(DriverProfileViewForPassenger.this).load(uri).into(imageView4);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //     Handle any errors
                }
            });

        }
    }
}

