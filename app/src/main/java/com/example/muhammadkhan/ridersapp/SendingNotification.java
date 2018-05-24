package com.example.muhammadkhan.ridersapp;

import android.os.AsyncTask;
import android.util.Log;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Muhammad Khan on 15/05/2018.
 */

public class SendingNotification extends AsyncTask<String,Void,Void> {
    @Override
    protected Void doInBackground(String... params) {
        String userContact=params[0];
        String message=params[1];
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("contact",userContact)
                    .add("message",message)
                    .build();
            Request request = new okhttp3.Request.Builder()
                    .url("http://172.19.0.150/push.php")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            Log.i("status", "doInBackground: "+response.message());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
