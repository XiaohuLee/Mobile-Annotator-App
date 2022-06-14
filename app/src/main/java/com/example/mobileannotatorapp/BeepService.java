package com.example.mobileannotatorapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BeepService extends Service {
    Alarm alarm = new Alarm();

    public void onCreate() {
        Log.d("BeepService: ", "onCreate");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarm.setAlarm(this, 1);
        return super.onStartCommand(intent,flags,startId);
    }




}