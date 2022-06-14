package com.example.mobileannotatorapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Auto restart if the phone is rebooted.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootCompletedReceiver: ", "onReceive");
        context.startActivity(new Intent(context, MainActivity.class));

    }

}