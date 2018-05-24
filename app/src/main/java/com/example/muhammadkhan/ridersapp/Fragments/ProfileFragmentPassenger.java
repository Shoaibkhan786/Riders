package com.example.muhammadkhan.ridersapp.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.muhammadkhan.ridersapp.Models.Passenger;
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
import com.valdesekamdem.library.mdtoast.MDToast;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Muhammad Khan on 05/04/2018.
 */
public class ProfileFragmentPassenger extends Fragment {
    private DatabaseReference firebaseDatabase;
    private FirebaseStorage storage;
    private StorageReference storageRef,imageRef;
    private UploadTask uploadTask;
    private static final int RESULT_LOAD_IMAGE = 1;
    private Uri imageUri=null;
    String city;
    //views
    private EditText userName;
    private EditText currentAddress;
    private EditText destinationAddress;
    private TextView contact;
    String passengerCity;
    private Button saveChanges;
    private CircleImageView imageView;
    //model class object
    Passenger passenger;
    ProgressDialog progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_passenger_profile,container,false);
        userName=(EditText) view.findViewById(R.id.passenger_name);
        currentAddress=(EditText) view.findViewById(R.id.current_address);
        destinationAddress=(EditText) view.findViewById(R.id.dest_address);
        contact=(TextView) view.findViewById(R.id.contact_info);
        imageView=(CircleImageView) view.findViewById(R.id.passenger_image);
        saveChanges=(Button) view.findViewById(R.id.saveChanges);
        //methods call
        showProgressBar();
        //background thread for profile retrieval
        Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                retrieveProfileInformation();
            }
        });
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges(view);
            }
        });
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
        return view;
    }
    //-------------------method for saving passenger data on firebase---------------------------------------------//
    public void saveChanges(View view){
        int count=1;
        if(checkUserInputs()==0){
            if(imageUri!=null) {
                passenger.setProfileImage(imageUri.toString());
                count=2;
            }
            if(!(passenger.getName().equals(userName.getText().toString()))){
                passenger.setName(userName.getText().toString());
                count=2;
            }
            if(!(passenger.getCurrentAddress().equals(currentAddress.getText().toString()))){
                passenger.setCurrentAddress(currentAddress.getText().toString());
                count=2;
            }
            if(!(passenger.getDestinationAdress().equals(destinationAddress.getText().toString()))){
                passenger.setDestinationAdress(destinationAddress.getText().toString());
                count=2;
            }

            if(count!=1){
                firebaseDatabase=FirebaseDatabase.getInstance().getReference("Passengers").child(passengerCity).child(FirebaseAuth.getInstance()
                        .getCurrentUser().getPhoneNumber());
                firebaseDatabase.setValue(passenger);
                //toast message
                MDToast mdToast = MDToast.makeText(getActivity(), "User profile has been updated", MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS);
                mdToast.show();
            }
        }
    }
    //-------------------method for checking user inputs----------------------------------------------------------//
    public int  checkUserInputs() {
        int count=0;
        if(TextUtils.isEmpty(userName.getText().toString().trim())){
            //showing  empty email message
            userName.setError("The item  Name can not be empty.");
            count=1;
        }
        if(TextUtils.isEmpty(currentAddress.getText().toString().trim())){
            //showing  empty email message
            currentAddress.setError("The item current address can not be empty.");
            count=1;
        }
        if(TextUtils.isEmpty(destinationAddress.getText().toString().trim())){
            //showing empty password message
            destinationAddress.setError("The item destination address can not be empty.");
            count=1;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode== RESULT_OK && data!=null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            uploadImageOnServer();
        }
    }
    //-------------------------------------------Method for uploading user picture on firebase server----------------------------------//
    public void uploadImageOnServer() {
        //image uploading
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imageRef = storageRef.child("Passenger").child(imageUri.getLastPathSegment());
        uploadTask=imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                imageUri = taskSnapshot.getDownloadUrl();
                                            }
                                        }
        );
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Sorry could not upload the image",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void retrieveProfileInformation(){
        //getting passenger city name
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        passengerCity= preferences.getString("passenger_city", "");
        Log.i("TAG :",passengerCity);

        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Passengers").child(passengerCity).child(currentUser);
        System.out.println(reference);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                passenger=dataSnapshot.getValue(Passenger.class);
                if(passenger.getProfileImage().equals("null")){
                    imageView.setImageResource(R.mipmap.profile);
                }else{
                    Glide.with(getActivity()).load(Uri.parse(passenger.getProfileImage())).into(imageView);
                }
                userName.setText(passenger.getName());
                currentAddress.setText(passenger.getCurrentAddress());
                destinationAddress.setText(passenger.getDestinationAdress());
                contact.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                //dimiss progress bar
                progressBar.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Sorry could not upload the image",Toast.LENGTH_LONG).show();
            }
        });
    }
        public void showProgressBar() {
            progressBar = new ProgressDialog(getActivity());
            progressBar.setMessage("wait");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.show();
        }
}
