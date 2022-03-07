package location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LocationTrack implements LocationListener {

    private static final String TAG = "10";
    Context context;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    Location location = null;
    Double latitude;
    Double longitude;
    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;

    public LocationTrack(Context context) {
        this.context = context;
        getLocation();
       // Toast.makeText(context, "Initialize", Toast.LENGTH_SHORT).show();
    }

    public Double getLatitude() {


        return latitude;
    }

    public Double getLongitude() {

        return longitude;
    }

    private void getLocation() {
        try {
            Log.i(TAG, "Sending Location to Server started");
           // Toast.makeText(context, "Update location\n" + MainActivity.userID + "\n" + MainActivity.child, Toast.LENGTH_LONG).show();
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            if (isGPSEnable == false && isNetworkEnable == false) {
              //  Toast.makeText(context, "Location not update", Toast.LENGTH_SHORT).show();
                location = null;

            } else {

                if (isNetworkEnable) {
                    if (ActivityCompat.checkSelfPermission(context
                            , Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context
                            , Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {


                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                1, 1, this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {
                           //     Toast.makeText(context, "Network provider", Toast.LENGTH_SHORT).show();
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                } else if (isGPSEnable) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1, 1, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null) {
                           // Toast.makeText(context, "GPS provider", Toast.LENGTH_SHORT).show();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

            }
        } catch (Exception e) {
          //  Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}
