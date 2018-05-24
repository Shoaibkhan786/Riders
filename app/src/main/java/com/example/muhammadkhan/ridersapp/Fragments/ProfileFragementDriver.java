package com.example.muhammadkhan.ridersapp.Fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muhammadkhan.ridersapp.Activities.ImageUpload;
import com.example.muhammadkhan.ridersapp.Models.Driver;
import com.example.muhammadkhan.ridersapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.File;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

/**
 * Created by Muhammad Khan on 03/04/2018.
 */

public class ProfileFragementDriver extends Fragment {
    String [] imageUrl=new String[4];
    //Firebase stuff
    UploadTask uploadTask;
    //firebase storage reference
    FirebaseStorage storage;
    StorageReference storageRef,imageRef;


    private static final int RESULT_LOAD_IMAGE=15;
    Uri userProfileUri=null;
    //linear layout
    private LinearLayout linear1;
    private LinearLayout linear2;
    //text view
    private TextView vehicleTextview;
    //Edit texts
    private EditText driverName;
    private EditText modelName;
    private EditText totalCpacity;
    private EditText space;
    private String driverCity;
    private String gari;
    private EditText pricePerKilometer;
    //image views
    private ImageView driverImage;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private Spinner spinner;
    private Button saveChanges;
    private ProgressDialog progressDialog;
    //toast message
    MDToast mdToast;
    private ArrayList<String> pickPhoto=new ArrayList<>();
    Driver driverInfo=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_driver_registeration,container,false);
        //getting linear layouts
        linear1=(LinearLayout) view.findViewById(R.id.linearLayout1);
        linear2=(LinearLayout) view.findViewById(R.id.linearLayout2);
        //listener on above layouts
        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickVehiclePhoto(view);

            }
        });
        linear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickVehiclePhoto(view);
            }
        });
       //getting textview reference
        vehicleTextview=(TextView) view.findViewById(R.id.vehicle_texview);
        vehicleTextview.setVisibility(View.GONE);
        // getting Imageviews references
        imageView1=(ImageView) view.findViewById(R.id.first_image);
        imageView2=(ImageView) view.findViewById(R.id.second_image);
        imageView3=(ImageView) view.findViewById(R.id.third_image);
        imageView4=(ImageView) view.findViewById(R.id.fourth_image);
        driverImage=(ImageView) view.findViewById(R.id.image);
        //getting edit text reference
        driverName=(EditText) view.findViewById(R.id.driver_name);
        modelName=(EditText) view.findViewById(R.id.model_name);
        totalCpacity=(EditText) view.findViewById(R.id.total_capacity);
        space=(EditText) view.findViewById(R.id.space);
        pricePerKilometer=(EditText) view.findViewById(R.id.price_per_kilo);
        saveChanges=(Button) view.findViewById(R.id.register);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);

        progressDialog=new ProgressDialog(getActivity());
        //method call for retrieving profile data
        Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage("Loading");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                retrieveData();
            }
        });

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
            public void onClick(View v) {
                // TODO Auto-generated method stub
                modelName.setError(null);
            }
        });
        totalCpacity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                totalCpacity.setError(null);
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
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count=0;
                if(checkUserInput()==0){
                    if(userProfileUri!=null){
                        driverInfo.setProfileImage(userProfileUri.toString());
                        count=1;
                    }if(!(driverName.getText().toString().equals(driverInfo.getDriverName()))){
                        driverInfo.setDriverName(driverName.getText().toString());
                        count=1;
                    }if(!(totalCpacity.getText().toString().equals(driverInfo.getTotalCapacity()))){
                        driverInfo.setTotalCapacity(totalCpacity.getText().toString());
                        count=1;
                    }
                    if (!(space.getText().toString().equals(driverInfo.getSpace()))){
                        driverInfo.setSpace(space.getText().toString());
                        count=1;
                    }if (!(modelName.getText().toString().equals(driverInfo.getModelName()))){
                        driverInfo.setModelName(modelName.getText().toString());
                        count=1;
                    }if(!(pricePerKilometer.getText().toString().equals(driverInfo.getPricePerKilometer()))){
                        driverInfo.setPricePerKilometer(pricePerKilometer.getText().toString());
                        count=1;
                    }
                    if(count!=0){
                    //saving data on firebase
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Drivers");
                                databaseReference.child(driverCity).child(gari).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue(driverInfo);
                            }
                        }).start();
                        //toast message
                        MDToast mdToast = MDToast.makeText(getActivity(), "User profile has been updated", MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS);
                        mdToast.show();
                    }
                }else {
                    mdToast = MDToast.makeText(getActivity(), "Please fill all fields properly.", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                    mdToast.show();
                }
            }
        });
        return view;
    }

    public void uploadImageFromGallery() {
        //image gallery intent
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK ) {
            if(requestCode==RESULT_LOAD_IMAGE && data!=null){
                userProfileUri=data.getData();
                Picasso.with(getActivity()).load(userProfileUri).into(driverImage);
                //method call
                uploadProfileImage();
                return;
            }
            pickPhoto.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
            if(pickPhoto.size()==4) {
                Glide.with(getActivity()).load(new File(pickPhoto.get(0))).into(imageView1);
                Glide.with(getActivity()).load(new File(pickPhoto.get(1))).into(imageView2);
                Glide.with(getActivity()).load(new File(pickPhoto.get(2))).into(imageView3);
                Glide.with(getActivity()).load(new File(pickPhoto.get(3))).into(imageView4);
                //getting images uri
                imageUrl[0] = Uri.parse(pickPhoto.get(0)).toString();
                imageUrl[1] = Uri.parse(pickPhoto.get(1)).toString();
                imageUrl[2] = Uri.parse(pickPhoto.get(2)).toString();
                imageUrl[3] = Uri.parse(pickPhoto.get(3)).toString();
                //background thread for image uploading
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //method call
                        ImageUpload imageUpload=new ImageUpload(getActivity());
                        imageUpload.uploadImageOnServer(pickPhoto);
                    }
                }).start();

            }else{
                mdToast = MDToast.makeText(getActivity(), "At least 4 picture can be uploaded", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
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
                mdToast = MDToast.makeText(getActivity(), "Sorry,could not upload pictures", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                mdToast.show();
            }
        });


    }
    public int checkUserInput() {
        int count=0;
        if(TextUtils.isEmpty(driverName.getText().toString().trim())){
            //showing  empty email message
            driverName.setError("The item  Name can not be empty.");
            count=1;
        }
        if(TextUtils.isEmpty(modelName.getText().toString().trim())){
            //showing  empty email message
            modelName.setError("The item Vehicle Type  can not be empty.");
            count=1;
        }
        if(TextUtils.isEmpty(totalCpacity.getText().toString().trim())){
            //showing empty password message
            totalCpacity.setError("The item Space Available can not be empty.");
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

    public void retrieveData() {
        //getting user registered vehicle
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        gari= preferences.getString("vehicle", "");
        driverCity=preferences.getString("currentCity","");
        //System.out.println("driver city is "+driverCity);
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Drivers").child(driverCity).child(gari).child(currentUser);
        System.out.println(reference);
        Log.i("GARI",currentUser);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                driverInfo=dataSnapshot.getValue(Driver.class);
                driverName.setText(driverInfo.getDriverName());
                modelName.setText(driverInfo.getModelName());
                totalCpacity.setText(driverInfo.getTotalCapacity());
                space.setText(driverInfo.getSpace());
                pricePerKilometer.setText(driverInfo.getPricePerKilometer());
                //driver did not upload his/her profile picture
                if(driverInfo.getProfileImage().equals("No")) {
                    driverImage.setImageResource(R.mipmap.profile);
                }else{
                    Glide.with(getActivity()).load(driverInfo.getProfileImage()).into(driverImage);
                }
                storage=FirebaseStorage.getInstance();
                for(int i=0;i<4;i++) {
                    storageRef = storage.getReference("Driver_Vehicles/"+currentUser+"/"+String.valueOf(i));
                    //Log.i("TAG",""+storageRef);
                    final int finalI =i;
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            int index=finalI;
                            if(index==0){
                               Glide.with(getActivity()).load(uri).into(imageView1);
                            }
                            if(index==1){
                                Glide.with(getActivity()).load(uri).into(imageView2);
                                return;
                            }
                            if(index==2){
                                Glide.with(getActivity()).load(uri).into(imageView3);
                            }
                            if(index==3){
                                Glide.with(getActivity()).load(uri).into(imageView4);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        //     Handle any errors
                        }
                    });
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void pickVehiclePhoto(View view){
        pickPhoto.clear();
        FilePickerBuilder.getInstance().setMaxCount(4)
                .setSelectedFiles(pickPhoto)
                .pickPhoto(ProfileFragementDriver.this);
    }
}
