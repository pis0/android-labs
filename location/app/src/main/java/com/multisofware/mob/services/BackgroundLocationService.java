package com.multisofware.mob.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class BackgroundLocationService extends Service {

    private static String TAG = "BackgroundLocationService";

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 5000;
    private static final float LOCATION_DISTANCE = 10f;
    private static final long LOCATION_CHANGED_CALL_MAX_THRESHOLD_TIME = 2000;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        LocationListener(String provider) {
            Log.d(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            //Log.d(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged: " + provider);
        }
    }


    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    @Override
    public void onCreate() {

        Log.d(TAG, "onCreate");

        initializeLocationManager();
        startLocationUpdates();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                requestLocationUpdates();
                handler.postDelayed(runnable, LOCATION_INTERVAL);
            }
        };

        handler.postDelayed(runnable, LOCATION_INTERVAL);

    }

    private void requestLocationUpdates() {

        //Log.d(TAG, "requestLocationUpdates");

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (Exception e) {
            Log.d(TAG, "requestLocationUpdates error:" + e.getMessage());
        }


        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (Exception e) {
            Log.d(TAG, "requestLocationUpdates error:" + e.getMessage());
        }


        try {
            getFusedLocationProviderClient(context).requestLocationUpdates(
                    locationRequest,
                    new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            onLocationChanged(locationResult.getLastLocation());
                        }
                    },
                    Looper.myLooper());
        } catch (Exception e) {
            Log.d(TAG, "requestLocationUpdates error:" + e.getMessage());
        }


        getLastLocation();

    }

    private void initializeLocationManager() {
        Log.d(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }


    private LocationRequest locationRequest;

    protected void startLocationUpdates() {

        Log.d(TAG, "startLocationUpdates");

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_INTERVAL);
        locationRequest.setMaxWaitTime(LOCATION_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.setAlwaysShow(true);
        builder.addLocationRequest(locationRequest);

        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

    }

    public void getLastLocation() {

        getFusedLocationProviderClient(this).getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) onLocationChanged(location);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });


        Location location;
        if (mLocationManager != null) {

            try {
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) onLocationChanged(location);
            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            }

            try {
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) onLocationChanged(location);
            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            }
        }

    }

    private double latitude;
    private double longitude;
    private long onLocationChangedCallThresholdTime;

    public void onLocationChanged(Location location) {

        Log.d(TAG, "calling onLocationChanged...");

        if (location.getLatitude() != latitude || location.getLongitude() != longitude) {

            latitude = location.getLatitude();
            longitude = location.getLongitude();

            long currentThresholdTime = new Date().getTime();
            if (Math.abs(currentThresholdTime - onLocationChangedCallThresholdTime) >= LOCATION_CHANGED_CALL_MAX_THRESHOLD_TIME)
                onLocationChangedCallThresholdTime = currentThresholdTime;
            else return;

            //Log.e(TAG,  latitude + "," + longitude, new Error());
            Log.w(TAG,  latitude + "," + longitude, new Error());
        }
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

//        super.onDestroy();
//        if (mLocationManager != null) {
//            for (int i = 0; i < mLocationListeners.length; i++) {
//                try {
//                    mLocationManager.removeUpdates(mLocationListeners[i]);
//                } catch (Exception ex) {
//                    Log.i(TAG, "fail to remove location listners, ignore", ex);
//                }
//            }
//        }
    }


}