package com.example.childmonitoringchildversion;

import android.content.Context;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import callhistory.CallHistoryTrack;
import contact.ContactTrack;
import location.LocationTrack;
import photo.PhotosTrack;
import sms.SMSTrack;

public class UpdateChildData extends Worker{

    private static final String TAG = UpdateChildData.class.getSimpleName();
    public static String child,userID;
    DateFormat dtf = new SimpleDateFormat("dd/MM/yyyy,HH:mm:ss");
    Date date = new Date();
    LocationTrack locationServices;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference ;
    SharedPreferences setSharedPreferences;

    public UpdateChildData(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        try {
            // get child name userID
            setSharedPreferences = getApplicationContext().getSharedPreferences("ChildInfo", Context.MODE_PRIVATE);
            child = setSharedPreferences.getString("childName", "");
            userID = setSharedPreferences.getString("userID", "");

            databaseReference = firebaseDatabase.getReference(userID).child("Child").child(child);
            databaseReference.child("LastUpdate").setValue(dtf.format(date) + "");
            doWork();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Result doWork() {

        Context context = getApplicationContext();
        try {
            updatePhoto(context);
            updateLocation(context);
            updateCallLog(context);
            updateSmsHistory(context);
            updateContactList(context);
           // Log.i(TAG, "Sending data to Server");
           // Log.i("user data", child + " , " + userID);
        }
        catch (Exception e){
         //   Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Result.retry();
        }
        return Result.success();
    }

    private void updatePhoto(Context context) {

        new PhotosTrack(context);
      //  Log.i("Update : " , "Photo");

    }

    private void updateContactList(Context context) {
        new ContactTrack(context);
        //Log.i("Update : " , "contact list");
    }

    private void updateCallLog(Context context) {
        new CallHistoryTrack(context);
        //Log.i("Update : " , "call log");
    }

    private void updateLocation(Context context) {
        try {

            locationServices = new LocationTrack(context);

            if(locationServices.getLatitude() != null && locationServices.getLongitude() != null) {

                databaseReference.child("Location").child("Latitude").setValue(locationServices.getLatitude() + "");
                databaseReference.child("Location").child("Longitude").setValue(locationServices.getLongitude() + "");
               // Log.i("Update : ", "Location ");
            }

        } catch (Exception e) {
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void updateSmsHistory(Context context){
        new SMSTrack(context);
        //Log.i("Update : " , "Sms history");
    }
}
