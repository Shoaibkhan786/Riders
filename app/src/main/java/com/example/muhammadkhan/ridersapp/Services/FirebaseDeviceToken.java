package com.example.muhammadkhan.ridersapp.Services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Muhammad Khan on 15/05/2018.
 */

public class FirebaseDeviceToken extends FirebaseInstanceIdService {
    private String recentToken=null;
    @Override
    public void onTokenRefresh() {
        recentToken= FirebaseInstanceId.getInstance().getToken();
    }
}
