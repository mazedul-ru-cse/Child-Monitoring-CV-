package com.example.childmonitoringchildversion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import calender.CustomCalendar;

public class MainActivity extends AppCompatActivity {

    EditText childName , parentEmail, parentPassword;
    AppCompatButton childAddBtn;
    FirebaseAuth firebaseAuth;
    Date date  = new Date();

    // Request code. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_CODE = 999;


    SharedPreferences setSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        getPermissions();

        setSharedPreferences = getSharedPreferences("ChildInfo", Context.MODE_PRIVATE);
        if(setSharedPreferences.getString("added","").equals("true")){
            MyService.activityShow = false;
            startActivity(new Intent(MainActivity.this , CustomCalendar.class));
            finish();
        }




        childName = findViewById(R.id.childName);
        parentEmail = findViewById(R.id.childParentEmail);
        parentPassword = findViewById(R.id.childParentPassword);
        childAddBtn = findViewById(R.id.childAdd);

         //Hide app icon
         // hideApp();

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

                            startActivity(new Intent(MainActivity.this , CustomCalendar.class));
                            finish();

                        }else {
                            Toast.makeText(MainActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

    }


    private void getPermissions(){

        Dexter.withContext(this)
                .withPermissions(

                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        //Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND,
                        Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                      // Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                       // Manifest.permission.ACCESS_MEDIA_LOCATION,
                       // Manifest.permission_group.CALL_LOG,
                        Manifest.permission_group.LOCATION,
                        Manifest.permission_group.SMS,
                        Manifest.permission_group.PHONE,
                        Manifest.permission_group.CONTACTS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                      //  Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission_group.CAMERA

                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if(multiplePermissionsReport.isAnyPermissionPermanentlyDenied()){

                }
                if(multiplePermissionsReport.areAllPermissionsGranted()){
                    Toast.makeText(MainActivity.this, multiplePermissionsReport.areAllPermissionsGranted()+"", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                permissionToken.continuePermissionRequest();
            }
        }).onSameThread().check();
    }


    public void help_link(View view) {
        TextView helpLink = findViewById(R.id.help);
        helpLink.setMovementMethod(LinkMovementMethod.getInstance());
    }
}