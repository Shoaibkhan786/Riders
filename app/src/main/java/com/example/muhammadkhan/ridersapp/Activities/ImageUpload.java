package com.example.muhammadkhan.ridersapp.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.muhammadkhan.ridersapp.Activities.DriverProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Muhammad Khan on 14/04/2018.
 */

public class ImageUpload {

    Context context;
    //Firebase stuff
    UploadTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageRef,imageRef;
    //toast message
    MDToast mdToast;

    public ImageUpload(Context context){
        this.context=context;
    }
    public void uploadImageOnServer(ArrayList<String> vehicleImages) {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        for(int i=0;i<vehicleImages.size();i++) {
            uploadMultipleImageOnFirebase(i,vehicleImages);
        }
    }
    public void uploadMultipleImageOnFirebase( final int index,ArrayList<String> vehicleImages){
        imageRef = storageRef.child("Driver_Vehicles").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                child(String.valueOf(index));
        //image uploading
        uploadTask=imageRef.putFile(Uri.fromFile(new File(vehicleImages.get(index))));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }});

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mdToast = MDToast.makeText(context, "Sorry,could not upload pictures", MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                mdToast.show();
            }
        });
    }

}
