package com.example.childmonitoringchildversion;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText childName , parentEmail, parentPassword;
    Button childAddBtn;
    FirebaseAuth firebaseAuth;

    // Request code. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_CODE = 999;

    String[] appPermissions = {
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission_group.CALL_LOG,
            Manifest.permission_group.LOCATION,
            Manifest.permission_group.SMS,
            Manifest.permission_group.PHONE,
            Manifest.permission_group.CONTACTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,

    };

    SharedPreferences setSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSharedPreferences = getSharedPreferences("ChildInfo", Context.MODE_PRIVATE);
        if(setSharedPreferences.getString("added","").equals("true")){
            startActivity(new Intent(MainActivity.this , BootUpServices.class));
            finish();
        }

        CheckAndRequestPermission();
        childName = findViewById(R.id.childName);
        parentEmail = findViewById(R.id.childParentEmail);
        parentPassword = findViewById(R.id.childParentPassword);
        childAddBtn = findViewById(R.id.childAdd);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        childAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Authenticate the user and add child
                firebaseAuth = firebaseAuth.getInstance();
                firebaseAuth.signInWithEmailAndPassword(parentEmail.getText().toString(),parentPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //Get current user UID
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userUID = user.getUid();

                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = firebaseDatabase.getReference(userUID).child("Child").child(childName.getText().toString());

                            databaseReference.child("Name").setValue(childName.getText().toString());

                            Toast.makeText(MainActivity.this, "Child added successfully", Toast.LENGTH_SHORT).show();

                            SharedPreferences setSharedPreferences1 = getSharedPreferences("ChildInfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor =  setSharedPreferences1.edit();
                            editor.putString("childName",childName.getText().toString());
                            editor.putString("userID",userUID);
                            editor.putString("added","true");
                            editor.apply();
                            startActivity(new Intent(MainActivity.this , BootUpServices.class));
                            finish();

                        }else {
                            Toast.makeText(MainActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

    }
    public void CheckAndRequestPermission() {
        //checking which permissions are granted
        List<String> listPermissionNeeded = new ArrayList<>();
        for (String item: appPermissions){
            if(ContextCompat.checkSelfPermission(this, item)!= PackageManager.PERMISSION_GRANTED)
                listPermissionNeeded.add(item);
        }

        //Ask for non-granted permissions
        if (!listPermissionNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),
                    PERMISSIONS_REQUEST_CODE);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if(grantResults[i]== PackageManager.PERMISSION_DENIED){
                    break;
                }
            }
        }
    }

}