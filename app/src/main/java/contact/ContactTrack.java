package contact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

import com.example.childmonitoringchildversion.UpdateChildData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import sms.SmsHistoryModel;

public class ContactTrack {

    private String contactName;
    private String contactNumber;


    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;

    public ContactTrack(Context context) {

        storeContact(context);

    }

    private void storeContact(Context context) {

        databaseReference = firebaseDatabase.getReference(UpdateChildData.userID).child("Child").child(UpdateChildData.child)
                .child("ContactList");
        databaseReference.removeValue();

        // reading all contact

            String sortOrder = Contacts.ContactMethods.DEFAULT_SORT_ORDER;

            Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                    null);
            int counter = 0;

        try {
            if ((cursor != null ? cursor.getCount() : 0) > 0) {

                while (cursor != null && cursor.moveToNext() && counter != 10) {

                    String id = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts._ID));

                    contactName = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                        Cursor pCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                        while (pCursor.moveToNext() && counter != 10) {

                            counter++;

                            contactNumber = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                            contactNumber =  contactNumber.replaceAll("[^a-zA-z0-9]", "");


                            //Toast.makeText(MainActivity.this, name + "\n"+ phoneNo, Toast.LENGTH_SHORT).show();
                            ContactModel contactModel = new ContactModel(contactName, contactNumber);
                            sendDataToServer(contactModel);



                        }
                        pCursor.close();
                    }

                }

            }


        }catch (Exception e) {
            e.printStackTrace();
        }

        if(cursor != null){
            cursor.close();
        }

    }


    private void sendDataToServer(ContactModel contactModel) {

        databaseReference = firebaseDatabase.getReference(UpdateChildData.userID).child("Child").child(UpdateChildData.child)
                .child("ContactList")
                .child(contactModel.getContactNumber());
        databaseReference.setValue(contactModel);
    }
}
