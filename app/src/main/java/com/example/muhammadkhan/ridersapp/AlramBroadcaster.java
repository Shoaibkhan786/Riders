package com.example.muhammadkhan.ridersapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Muhammad Khan on 16/05/2018.
 */

public class AlramBroadcaster extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Pick up time",Toast.LENGTH_LONG).show();
    }
}
