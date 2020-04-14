package com.multisofware.android.firebase;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.multisofware.android.camera.CameraPreview;

import java.util.List;

public class FirebaseTest extends AppCompatActivity {

//    private static String TAG = "QRCODE";
//    static int GALLERY_REQUEST_CODE = 1 << 0;

    private static final String TAG = FirebaseTest.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS = 1 << 0;

    private static final String[] PERMISSIONS = new String[]{
            android.Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

//        pickFromGallery();

        if (checkPermissions()) initCamera(); //dispatchTakePictureIntent();
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
                    //dispatchTakePictureIntent();
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

    private static android.hardware.Camera getCameraInstance() {
        android.hardware.Camera camera = null;
        try {
            camera = android.hardware.Camera.open();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return camera;
    }

    private android.hardware.Camera camera;
    private CameraPreview preview;
    private FrameLayout FLPreview;

    private void initCamera() {

        if (!checkCameraHardware(this)) {
            Log.e(TAG, "initCamera error: This device has no access to camera features");
            return;
        }

        // Create an instance of Camera
        camera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        preview = new CameraPreview(this, camera);
        FLPreview = (FrameLayout) findViewById(R.id.camera_preview);
        FLPreview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (FLPreview == v) {
                    Toast.makeText(getApplicationContext(), "I am clicked", Toast.LENGTH_SHORT).show();
                    camera.takePicture(null, null, pictureCallback);
                }
            }
        });
        FLPreview.addView(preview);

    }


    private android.hardware.Camera.PictureCallback pictureCallback = new android.hardware.Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
            Toast.makeText(getApplicationContext(), "onPictureTaken", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onPictureTaken - data:" + data);

            processData(data);

        }

    };

    private void processData(byte[] data) {

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        Log.d(TAG, "processData - bitmap:" + bitmap);

//        FirebaseVisionImageMetadata.Builder firebaseVisionImageMetadataBuilder = new FirebaseVisionImageMetadata.Builder();
//        firebaseVisionImageMetadataBuilder.setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21);
//        firebaseVisionImageMetadataBuilder.setRotation(FirebaseVisionImageMetadata.ROTATION_270);
//        firebaseVisionImageMetadataBuilder.setWidth(1024);
//        firebaseVisionImageMetadataBuilder.setHeight((int)(1024 * 0.75));
//        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromByteArray(
//                data,
//                firebaseVisionImageMetadataBuilder.build()
//        );




        FirebaseVisionBarcodeDetector detector = FirebaseVision
                .getInstance()
                .getVisionBarcodeDetector(setOptions());


        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {

                        Log.d(TAG, "onSuccess: " + barcodes);

                        for (FirebaseVisionBarcode barcode : barcodes) {
//                                        Rect bounds = barcode.getBoundingBox();
//                                        Point[] corners = barcode.getCornerPoints();
//                                        String rawValue = barcode.getRawValue();
                            int valueType = barcode.getValueType();

                            Log.d(TAG, "valueType: " + valueType + ", barcode:" + barcode.toString());

                            switch (valueType) {
                                case FirebaseVisionBarcode.FORMAT_QR_CODE:
                                case FirebaseVisionBarcode.TYPE_TEXT:

//                                                String ssid = barcode.getWifi().getSsid();
//                                                String password = barcode.getWifi().getPassword();
//                                                int type = barcode.getWifi().getEncryptionType();

                                    String temp = barcode.getDisplayValue();

                                    Log.d(TAG, "temp: " + temp);


                                    break;
                            }


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "error: " + e.getMessage(), e);
                    }
                });

    }


    private FirebaseVisionBarcodeDetectorOptions setOptions() {
        return new FirebaseVisionBarcodeDetectorOptions
                .Builder()
                .setBarcodeFormats(
                        FirebaseVisionBarcode.FORMAT_QR_CODE,
                        FirebaseVisionBarcode.TYPE_TEXT
                )
                .build();
    }


//    private void pickFromGallery() {
//
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        String[] mimeTypes = {"image/jpeg", "image/png"};
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        startActivityForResult(intent, GALLERY_REQUEST_CODE);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == GALLERY_REQUEST_CODE) {
//
//                Uri selectedImage = Objects.requireNonNull(data).getData();
//                try {
//
//                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(Objects.requireNonNull(selectedImage), "r");
//                    FileDescriptor fileDescriptor = Objects.requireNonNull(parcelFileDescriptor).getFileDescriptor();
//                    Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//                    parcelFileDescriptor.close();
//
//                    Log.d(TAG, "image: " + image);
//
//                    FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(image);
//
//                    FirebaseVisionBarcodeDetector detector = FirebaseVision
//                            .getInstance()
//                            .getVisionBarcodeDetector(setOptions());
//
//
//                    Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(firebaseVisionImage)
//                            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
//                                @Override
//                                public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
//
//                                    Log.d(TAG, "onSuccess: " + barcodes);
//
//                                    for (FirebaseVisionBarcode barcode : barcodes) {
////                                        Rect bounds = barcode.getBoundingBox();
////                                        Point[] corners = barcode.getCornerPoints();
////                                        String rawValue = barcode.getRawValue();
//                                        int valueType = barcode.getValueType();
//
//                                        Log.d(TAG, "valueType: " + valueType + ", barcode:" + barcode.toString());
//
//                                        switch (valueType) {
//                                            case FirebaseVisionBarcode.FORMAT_QR_CODE:
//                                            case FirebaseVisionBarcode.TYPE_TEXT:
//
////                                                String ssid = barcode.getWifi().getSsid();
////                                                String password = barcode.getWifi().getPassword();
////                                                int type = barcode.getWifi().getEncryptionType();
//
//                                                String temp = barcode.getDisplayValue();
//
//                                                Log.d(TAG, "temp: " + temp );
//
//
//                                                break;
//                                        }
//
//
//                                    }
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.e(TAG, "error: " + e.getMessage(), e);
//                                }
//                            });
//
//
//                } catch (IOException err) {
//                    Log.e(TAG, "error: " + err.getMessage(), err);
//                    err.printStackTrace();
//                }
//            }
//        }
//    }

//
//    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
//    static {
//        ORIENTATIONS.append(Surface.ROTATION_0, 90);
//        ORIENTATIONS.append(Surface.ROTATION_90, 0);
//        ORIENTATIONS.append(Surface.ROTATION_180, 270);
//        ORIENTATIONS.append(Surface.ROTATION_270, 180);
//    }
//
//    /**
//     * Get the angle by which an image must be rotated given the device's current
//     * orientation.
//     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private int getRotationCompensation(String cameraId, Activity activity, Context context)
//            throws CameraAccessException {
//        // Get the device's current rotation relative to its "native" orientation.
//        // Then, from the ORIENTATIONS table, look up the angle the image must be
//        // rotated to compensate for the device's rotation.
//        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//        int rotationCompensation = ORIENTATIONS.get(deviceRotation);
//
//        // On most devices, the sensor orientation is 90 degrees, but for some
//        // devices it is 270 degrees. For devices with a sensor orientation of
//        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
//        CameraManager cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
//        int sensorOrientation = cameraManager
//                .getCameraCharacteristics(cameraId)
//                .get(CameraCharacteristics.SENSOR_ORIENTATION);
//        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;
//
//        // Return the corresponding FirebaseVisionImageMetadata rotation value.
//        int result;
//        switch (rotationCompensation) {
//            case 0:
//                result = FirebaseVisionImageMetadata.ROTATION_0;
//                break;
//            case 90:
//                result = FirebaseVisionImageMetadata.ROTATION_90;
//                break;
//            case 180:
//                result = FirebaseVisionImageMetadata.ROTATION_180;
//                break;
//            case 270:
//                result = FirebaseVisionImageMetadata.ROTATION_270;
//                break;
//            default:
//                result = FirebaseVisionImageMetadata.ROTATION_0;
//                Log.e(TAG, "Bad rotation value: " + rotationCompensation);
//        }
//        return result;
//    }


}
