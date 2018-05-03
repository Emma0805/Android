package com.example.gu.knowyourgovernment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;


public class Locator {

    private MainActivity mainActivity;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public Locator(MainActivity activity) {
        mainActivity = activity;

        if (checkPermission()) {
            setUpLocationManager();
            determineLocation();
        }
    }

    public void setUpLocationManager() {

        if (locationManager != null)
            return;

        if (!checkPermission())
            return;

        // Get the system's Location Manager
        locationManager = (LocationManager) mainActivity.getSystemService(LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Toast.makeText(mainActivity, "Update from " + location.getProvider(), Toast.LENGTH_SHORT).show();
                mainActivity.doLocationWork(location.getLatitude(), location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Nothing to do here
            }

            public void onProviderEnabled(String provider) {
                // Nothing to do here
            }

            public void onProviderDisabled(String provider) {
                // Nothing to do here
            }
        };
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    public void shutdown() {
        locationManager.removeUpdates(locationListener);
        locationManager = null;
    }

    // This method chooses the best location provider Network ==> Passive ==> GPS
    public void determineLocation() {

        if (!checkPermission())
            return;

        if (locationManager == null)
            setUpLocationManager();

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) {
                Toast.makeText(mainActivity, "Using " + LocationManager.NETWORK_PROVIDER + " Location provider", Toast.LENGTH_SHORT).show();
                mainActivity.doLocationWork(loc.getLatitude(), loc.getLongitude());
                return;
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (loc != null) {
                Toast.makeText(mainActivity, "Using " + LocationManager.PASSIVE_PROVIDER + " Location provider", Toast.LENGTH_SHORT).show();
                mainActivity.doLocationWork(loc.getLatitude(), loc.getLongitude());
                return;
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                Toast.makeText(mainActivity, "Using " + LocationManager.GPS_PROVIDER + " Location provider", Toast.LENGTH_SHORT).show();
                mainActivity.doLocationWork(loc.getLatitude(), loc.getLongitude());
                return;
            }
        }
        mainActivity.noLocationAvailable();
        return;
    }


    // This method asks the user for Locations permissions (if it does not already have them)
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
            return false;
        }
        return true;
    }
}
