package com.example.childmonitoringchildversion;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import calender.CustomCalendar;

public class MyService extends BroadcastReceiver {
    public static boolean activityShow = false;

    public static String SHOW_APP_CODE = "*1234#";
    @Override
    public void onReceive(Context context, Intent intent) {

         //   String dialedNumber=null;// = getResultData();
           // if (dialedNumber == null) {
                // No reformatted number, use the original
            //    dialedNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
          //  }

           // Log.d("check","Working");
          //  Toast.makeText(context, "Work", Toast.LENGTH_SHORT).show();
            //Log.d("Dialed Number: ", dialedNumber);
             // if (dialedNumber.equals(SHOW_APP_CODE)) {
//                PackageManager packageManager = context.getPackageManager();
//                ComponentName componentName = new ComponentName(context, BootUpServices.class);
//                packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                        PackageManager.DONT_KILL_APP);

                //Intent to launch BootUpServices
                activityShow = true;
                Intent bootUpServices = new Intent(context, CustomCalendar.class);
                bootUpServices.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(bootUpServices);

                // My app will bring up, so cancel the dialer broadcast
               // setResultData(null);

           // }

    }

}
