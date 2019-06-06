package ca.mcgill.ecse321.project6.treeple_android;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

/*
Base for any activity that needs location services.
Basically keeps a lot of the permission checking and setting
latitude/longitude in one place. Makes things easier to edit.
 */
public abstract class LocationActivity extends AppCompatActivity {
    double latitude = Double.NaN;
    double longitude = Double.NaN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            finish();   // will end the activity and return to the main activity for the app
        }
    }

}
