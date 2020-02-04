package com.multisofware.mob;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.multisofware.mob.services.BackgroundLocationService;

public class Location extends AppCompatActivity {

    private static String TAG = "Location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

    }

    private static final int BACKGROUND_LOCATION_REQUEST_CODE = 1 << 0;
    private static final int FULL_LOCATION_REQUEST_CODE = 1 << 1;

    private void checkPermissions() {
        boolean permissionAccessCoarseLocationApproved = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessCoarseLocationApproved) {

            boolean backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED;

            if (backgroundLocationPermissionApproved) {

                Log.d(TAG, "start the service!");
                startService(new Intent(this, BackgroundLocationService.class));

            } else {

                Log.d(TAG, "only foreground location access");

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        },
                        BACKGROUND_LOCATION_REQUEST_CODE
                );
            }
        } else {

            Log.d(TAG, "no location access");

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    },
                    FULL_LOCATION_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "requestCode:" + requestCode + ", permissions:" + permissions + ", grantResults:" + grantResults);

        switch (requestCode) {
            case FULL_LOCATION_REQUEST_CODE:
            case BACKGROUND_LOCATION_REQUEST_CODE: {

                for (int results : grantResults)
                    if (results != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "has no permissions enough to start the service");
                        requestPermissionsAgain();
                        return;
                    }

                Log.d(TAG, "start the service!");
                startService(new Intent(this, BackgroundLocationService.class));
            }
        }


    }

    private AlertDialog alert = null;
    private void requestPermissionsAgain() {

        if(alert == null)
            alert = new AlertDialog.Builder(this)
                    .setMessage(
                            "Tem q permitir saporra senão nao vai rodar essa caceta! Se em algum momento vc deu um \"negar e nao perguntar novamente\", vc se fudeu.." //R.string.dialog_message
                    )
                    .setTitle(
                            "Ow, carai! :\\" //R.string.dialog_title
                    )
                    .setPositiveButton(
                            "Tentar Novamente", //R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    checkPermissions();
                                }
                            }).create();
        alert.show();

    }

}
