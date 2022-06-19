package com.example.mobileannotatorapp;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.util.Collections.singleton;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.intentfilter.androidpermissions.PermissionManager;
import com.intentfilter.androidpermissions.models.DeniedPermission;
import com.intentfilter.androidpermissions.models.DeniedPermissions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class Alarm extends BroadcastReceiver {
    private static final String LOG_TAG = "Alarm:";
    private PermissionManager recordAudioPermissionManager = null;
    private PermissionManager writeExternalStoragePermissionManager = null;
    private Boolean result1 = false; //record Audio
    private Boolean result2 = false; //write External Storage
    // creating a variable for medi recorder object class.
    private MediaRecorder mRecorder;

    // creating a variable for mediaplayer class
    private MediaPlayer mPlayer;
    private static int count;

    // string variable is created for storing a file name
    private static String mFileName = null;

    private Timer timer;
    private TimerTask timerTask;


    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG + "wake lock");
        wl.acquire();

        Log.d(LOG_TAG, "onReceive");
        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
        audioRecord(context);

        wl.release();

    }

    public void setAlarm(Context context, int min)
    {
        count = 0;
        AlarmManager am =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        //PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        PendingIntent pi;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pi = PendingIntent.getBroadcast(context,
                    0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        }else {
            pi = PendingIntent.getBroadcast(context,
                    0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        }
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 1000 * 60 * min, pi); // Millisec * Second * Minute


    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private void audioRecord(Context context) {
        Log.d(LOG_TAG, "Audio Record");


        if (CheckPermissions(context)) {
            Log.d(LOG_TAG, "Audio Record: Permission granted");
            startRecording();
        } else {
            Log.d(LOG_TAG, "Audio Record: Permission denied");
            requestAudioRecordPermission(context);
            requestWriteExternalStoragePermission(context);
        }

    }

    private void startTimer() {
        Log.d(LOG_TAG, "start timer");
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after 5000ms
        timer.schedule(timerTask, 5000);
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // below method will stop
                // the audio recording.
                mRecorder.stop();

                // below method will release
                // the media recorder class.
                mRecorder.release();
                mRecorder = null;

                // for playing our recorded audio
                // we are using media player class.
                mPlayer = new MediaPlayer();
                try {
                    Log.d(LOG_TAG, "initializeTimerTask: run");
                    // below method is used to set the
                    // data source which will be our file name
                    mPlayer.setDataSource(mFileName);

                    // below method will prepare our media player
                    mPlayer.prepare();

                    // below method will start our media player.
                    mPlayer.start();
                    //statusTV.setText("Recording Started Playing");
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        };
    }

    private void requestAudioRecordPermission(Context context) {
        recordAudioPermissionManager = PermissionManager.getInstance(context);
        recordAudioPermissionManager.checkPermissions(singleton(RECORD_AUDIO), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(context, "Record Audio Permissions Granted", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "getPermission: Record Audio Permission Granted");
                //result1 = true;
            }

            @Override
            public void onPermissionDenied(DeniedPermissions deniedPermissions) {
                String deniedPermissionsText = "Denied: " + Arrays.toString(deniedPermissions.toArray());
                Toast.makeText(context, deniedPermissionsText, Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "getPermission: Record Audio Permission Denied");
                //result1 = false;

                for (DeniedPermission deniedPermission : deniedPermissions) {
                    if(deniedPermission.shouldShowRationale()) {
                        // Display a rationale about why this permission is required
                        Toast.makeText(context, "Record Audio Permissions should be Granted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void requestWriteExternalStoragePermission(Context context) {
        writeExternalStoragePermissionManager = PermissionManager.getInstance(context);
        writeExternalStoragePermissionManager.checkPermissions(singleton(WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(context, "Write External Storage Permissions Granted", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "getPermission: Write External Storage Permission Granted");
                //result2 = true;
            }

            @Override
            public void onPermissionDenied(DeniedPermissions deniedPermissions) {
                String deniedPermissionsText = "Denied: " + Arrays.toString(deniedPermissions.toArray());
                Toast.makeText(context, deniedPermissionsText, Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "getPermission: Write External Storage Permission Denied");
                //result2 = false;

                for (DeniedPermission deniedPermission : deniedPermissions) {
                    if(deniedPermission.shouldShowRationale()) {
                        // Display a rationale about why this permission is required
                        Toast.makeText(context, "Write External Storage Permissions should be Granted", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }
    private boolean CheckPermissions(Context context) {
        // this method is used to check permission
        Log.d(LOG_TAG, "CheckPermissions");
        int result = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context, RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;

    }

    private void startRecording() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/AudioRecording" + count + ".3gp";
        Log.d(LOG_TAG, "mFileName: " + mFileName);

        // below method is used to initialize
        // the media recorder class
        mRecorder = new MediaRecorder();

        // below method is used to set the audio
        // source which we are using a mic.
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        // below method is used to set
        // the output format of the audio.
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        // below method is used to set the
        // audio encoder for our recorded audio.
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // below method is used to set the
        // output file location for our recorded audio
        mRecorder.setOutputFile(mFileName);
        try {
            // below method will prepare
            // our audio recorder class
            mRecorder.prepare();
         } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        // start method will start
        // the audio recording.
        mRecorder.start();
        startTimer();

    }






}