package com.multisofware.android.aws;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.multisofware.android.aws.camera.CameraPreview;
import com.multisofware.android.aws.camera.IAutoFocusCallback;
import com.multisofware.android.aws.view.BackButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraActivity extends AppCompatActivity {

    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS = 1 << 0;

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    private BackButton backButton;

    private TextView toastText;
    private Toast toast;

    private void createToast(Context context) {
        toastText = new TextView(context);
        toastText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toastText.setTextColor(0xffffffff);
        toastText.setWidth(600);

        toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);

        toast.setView(toastText);
    }

    private void showToast(String message) {
        toastText.setText(message);
        toast.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO to review
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE;
//        decorView.setSystemUiVisibility(uiOptions);

        createToast(this);

        if (checkPermissions()) initCamera();
        else ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                REQUEST_PERMISSIONS);
    }

    private boolean checkPermissions() {

        for (String permission : PERMISSIONS)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && checkPermissions()) {
                    Log.d(TAG, "onRequestPermissionsResult - success");
                    initCamera();
                } else {
                    Log.e(TAG, "onRequestPermissionsResult - failed");
                }
            }
        }
    }


    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private static android.hardware.Camera openFrontFacingCamera() {
        int cameraCount = 0;
        android.hardware.Camera cam = null;
        android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();
        cameraCount = android.hardware.Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            android.hardware.Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = android.hardware.Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }

    private static android.hardware.Camera getCameraInstance() {
        android.hardware.Camera camera = null;
        try {
            // regular back camera
            //camera = android.hardware.Camera.open();

            camera = openFrontFacingCamera();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return camera;
    }

    private android.hardware.Camera camera;
    private CameraPreview preview;
    private FrameLayout FLPreview;

    private Boolean takePictureLock = false;

    private void initCamera() {

        if (!checkCameraHardware(this)) {
            Log.e(TAG, "initCamera error: This device has no access to camera features");
            return;
        }

        camera = getCameraInstance();

        preview = new CameraPreview(this, camera);
        FLPreview = (FrameLayout) findViewById(R.id.camera_preview);

        FLPreview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (FLPreview == v && !takePictureLock) {

                    //TODO to review
                    preview.autoFocus(new IAutoFocusCallback() {
                        @Override
                        public void run(Boolean success) {
                            if (success) {
                                showToast("I am clicked");
                                camera.takePicture(null, null, pictureCallback);
                                takePictureLock = true;
                            }
                        }
                    });

                }
            }
        });

        FLPreview.addView(preview);
        backButton = new BackButton(this);
        FLPreview.addView(backButton);
    }


    private android.hardware.Camera.PictureCallback pictureCallback = new android.hardware.Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
            showToast("onPictureTaken");
            Log.d(TAG, "onPictureTaken - data:" + data);
            processData(data);
            camera.startPreview();
        }

    };

    private void processData(byte[] data) {
        try {

            Log.d(TAG, "processData - data.length:" + data.length);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);
            final int originalW = options.outWidth;
            final int originalH = options.outHeight;
            float scale = (float) MainActivity.MAX_SIZE / (float) originalH;
            options.inScaled = true;
            options.inSampleSize = MainActivity.resolveSampleSize(options, (int) (originalW * scale), MainActivity.MAX_SIZE);
            options.inMutable = false;
            options.inJustDecodeBounds = false;

            createImageFile(BitmapFactory.decodeByteArray(data, 0, data.length, options));


            takePictureLock = false;

        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }

    private void createImageFile(Bitmap original) throws IOException {

        final int QUALITY = 60;

        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File dir = new File(storageDir + "/tmp");
        if (!dir.exists()) dir.mkdir();
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                dir
        );

        File file = new File(image.getAbsolutePath());
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            original.compress(Bitmap.CompressFormat.JPEG, QUALITY, fOut);
            fOut.flush();
            fOut.close();

            original.recycle();

            Log.d(TAG, "createImageFile success - file: " + file.getAbsolutePath());

            Intent returnIntent = new Intent();
            returnIntent.putExtra("cameraData", Uri.fromFile(file).toString());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        } catch (Exception e) {
            Log.e(TAG, "createImageFile error: " + e.getMessage(), e);
        }


    }


//    private void createImageFile(String ticketNumber, Bitmap original) throws IOException {
//
//        Log.d(TAG, "createImageFile - Bitmap original - " + original.getWidth() + ", " + original.getHeight());
//
//        // Save image to gallery
//        String savedImageURL = MediaStore.Images.Media.insertImage(
//                getContentResolver(),
//                original,
//                "temp",
//                "temp"
//        );
//    }


}
