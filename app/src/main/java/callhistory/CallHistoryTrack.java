package callhistory;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import com.example.childmonitoringchildversion.UpdateChildData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CallHistoryTrack {

    private static final String TAG = "11";
    private String callNumber;
    private String contactName;
    private String callType;
    private String callDate;
    private String callDuration;
    private String callTime;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    public CallHistoryTrack(Context context) {
        storeCallHistory(context);
    }

    private void storeCallHistory(Context ctx) {
        databaseReference = firebaseDatabase.getReference(UpdateChildData.userID).child("Child").child(UpdateChildData.child)
                .child("CallLog");
        databaseReference.removeValue();

        Log.i(TAG, "Sending CallLog to Server started");
        // reading all data in descending order according to DATE
        String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";
        int callCounter = 0;
        String time , date ;

        Cursor cursor = ctx.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                sortOrder);

        //looping through the cursor to add data into arraylist
        while (cursor.moveToNext()){

            callCounter = callCounter + 1;

            callNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            contactName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            contactName = contactName==null || contactName.equals("") ? "Unknown" : contactName;
            callType = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
            callDate = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
            callDuration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));

            time = callDate;
            date = callDate;
            SimpleDateFormat dateFormatter = new SimpleDateFormat(
                    "dd-MM-yyyy");
            callDate = dateFormatter.format(new Date(Long.parseLong(date)));

            SimpleDateFormat timeFormatter = new SimpleDateFormat(
                    "HH:mm:ss");
            callTime = timeFormatter.format(new Date(Long.parseLong(time)));

            //str_call_time = getFormatedDateTime(str_call_time, "HH:mm:ss", "hh:mm ss");

            switch(Integer.parseInt(callType)){
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "Incoming";
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "Outgoing";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "Missed";
                    break;
                case CallLog.Calls.VOICEMAIL_TYPE:
                    callType = "Voice email";
                    break;
                case CallLog.Calls.REJECTED_TYPE:
                    callType = "Rejected";
                    break;
                case CallLog.Calls.BLOCKED_TYPE:
                    callType = "Blocked";
                    break;
                case CallLog.Calls.ANSWERED_EXTERNALLY_TYPE:
                    callType = "Externally Answered";
                    break;
                default:
                    callType = "NA";
            }

            callDuration = DurationFormat(callDuration);

            CallLogModel callLogItem = new CallLogModel(callNumber, contactName, callType,
                    callDate, callTime, callDuration);

            SendDataToServer(callLogItem);

            if(callCounter == 30)
                break;
        }
    }

    private void SendDataToServer(CallLogModel callLogItem) {
        databaseReference = firebaseDatabase.getReference(UpdateChildData.userID).child("Child").child(UpdateChildData.child)
                .child("CallLog")
                .child(callLogItem.getPhNumber());
        databaseReference.setValue(callLogItem);
    }

    private String DurationFormat(String duration) {
        String durationFormatted=null;
        if(Integer.parseInt(duration) < 60){
            durationFormatted = duration+" sec";
        }
        else{
            int min = Integer.parseInt(duration)/60;
            int sec = Integer.parseInt(duration)%60;

            if(sec==0)
                durationFormatted = min + " min" ;
            else
                durationFormatted = min + " min " + sec + " sec";

        }
        return durationFormatted;
    }
}
