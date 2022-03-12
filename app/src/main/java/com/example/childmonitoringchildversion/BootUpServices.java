package com.example.childmonitoringchildversion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class BootUpServices extends AppCompatActivity {

    private static final String TAG_SEND_DATA = "Sending data to server";
    SharedPreferences setSharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot_up_services);
        SettingUpPeriodicWork();
       // finish();
    }
    private void SettingUpPeriodicWork() {

        try {
            // Create Network constraint
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .setRequiresStorageNotLow(true)
                    .build();


            PeriodicWorkRequest periodicSendDataWork =
                    new PeriodicWorkRequest.Builder(UpdateChildData.class, 5, TimeUnit.MINUTES)
                            .addTag(TAG_SEND_DATA)
                            .setConstraints(constraints)
                            // setting a backoff on case the work needs to retry
                            .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                            .build();

            WorkManager workManager = WorkManager.getInstance(this);
            workManager.enqueue(periodicSendDataWork);
        } catch (Exception e) {
            e.printStackTrace();
           // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PackageManager packageManager = getPackageManager();
        ComponentName componentName = new ComponentName(BootUpServices.this, MainActivity.class);
        packageManager.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

}