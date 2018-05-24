package com.example.muhammadkhan.ridersapp.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.muhammadkhan.ridersapp.Activities.DriverMainActivity;
import com.example.muhammadkhan.ridersapp.Activities.PassengerMainActivity;
import com.example.muhammadkhan.ridersapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Muhammad Khan on 15/05/2018.
 */

public class MyFirebaseNotification extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("FCM", "From: " + remoteMessage.getFrom());
        //notification builder
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_notifications_black_24dp)
                        .setContentText(remoteMessage.getNotification().getBody());
            builder.setDefaults(Notification.DEFAULT_SOUND);
            Intent notificationIntent = new Intent(this, PassengerMainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Add as notification
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        }
}
