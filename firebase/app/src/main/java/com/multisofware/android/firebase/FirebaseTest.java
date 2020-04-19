package com.multisofware.android.firebase;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.multisofware.android.camera.CameraPreview;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class FirebaseTest extends AppCompatActivity {

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

    private static android.hardware.Camera getCameraInstance() {
        android.hardware.Camera camera = null;
        try {
            camera = android.hardware.Camera.open();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return camera;
    }

    private android.hardware.Camera cameraa;
    private CameraPreview preview;
    private FrameLayout FLPreview;

    private Boolean takePictureLock = false;

    private void initCamera() {

        if (!checkCameraHardware(this)) {
            Log.e(TAG, "initCamera error: This device has no access to camera features");
            return;
        }

        cameraa = getCameraInstance();

        preview = new CameraPreview(this, cameraa);
        FLPreview = (FrameLayout) findViewById(R.id.camera_preview);
        FLPreview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (FLPreview == v && !takePictureLock) {

                    Toast.makeText(getApplicationContext(), "I am clicked", Toast.LENGTH_SHORT).show();

                    cameraa.takePicture(null, null, new android.hardware.Camera.PictureCallback() {

                        @Override
                        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                            Toast.makeText(getApplicationContext(), "onPictureTaken", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onPictureTaken - data:" + data);

                            processData(data);
                            cameraa.startPreview();

                        }

                    });
                    takePictureLock = true;
                }
            }
        });

        FLPreview.addView(preview);
    }


    private void processData(byte[] data) {
        try {
            barCodeDetector(480, data);
//            textDetector(2048, data);
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
            takePictureLock = false;
        }
    }


    private FirebaseVisionImage createFirebaseVisionImage(int maxSize, byte[] data) {

        Bitmap originalBmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        int originalW = originalBmp.getWidth();
        int originalH = originalBmp.getHeight();

        Log.d(TAG, "processData - MAX_SIZE: " + maxSize + ", originalW: " + originalW + ", originalH: " + originalH);

        Bitmap bmpOut = Bitmap.createScaledBitmap(originalBmp,
                maxSize,
                (int) (((double) maxSize / (double) originalW) * (double) originalH),
                false);

        FirebaseVisionImage firebaseVisionImage = null;
        if (bmpOut != null) {
            firebaseVisionImage = FirebaseVisionImage.fromBitmap(bmpOut);
            Log.d(TAG, "processData - bmpOut:" + bmpOut + " - " + bmpOut.getWidth() + ", " + bmpOut.getHeight());
            return firebaseVisionImage;
        }

        return null;
    }


    // qrcode
    private FirebaseVisionBarcodeDetectorOptions setBarcodeDetectorOptions() {
        return new FirebaseVisionBarcodeDetectorOptions
                .Builder()
                .setBarcodeFormats(
                        FirebaseVisionBarcode.FORMAT_ALL_FORMATS
                )
                .build();
    }

    private void barCodeDetector(int maxSize, byte[] data) {

        FirebaseVisionImage firebaseVisionImage = createFirebaseVisionImage(maxSize, data);

        if (firebaseVisionImage == null) {
            Log.d(TAG, "barCodeDetector error: firebaseVisionImage is null");
            takePictureLock = false;
            return;
        }

        FirebaseVisionBarcodeDetector detector = FirebaseVision
                .getInstance()
                .getVisionBarcodeDetector(setBarcodeDetectorOptions());

        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        Log.d(TAG, "onSuccess: " + barcodes);
                        takePictureLock = false;
                        for (FirebaseVisionBarcode barcode : barcodes) {
                            int valueType = barcode.getValueType();
                            Log.d(TAG, "valueType: " + valueType + ", barcode:" + barcode.toString());
                            switch (valueType) {
                                case FirebaseVisionBarcode.FORMAT_QR_CODE:
                                case FirebaseVisionBarcode.TYPE_TEXT:
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
                        takePictureLock = false;
                    }
                });
    }

    private void textDetector(int maxSize, byte[] data) {

        FirebaseVisionImage firebaseVisionImage = createFirebaseVisionImage(maxSize, data);

        if (firebaseVisionImage == null) {
            Log.d(TAG, "textDetector error: firebaseVisionImage is null");
            takePictureLock = false;
            return;
        }

        FirebaseVisionTextRecognizer detector = FirebaseVision
                .getInstance()
                .getOnDeviceTextRecognizer();

        Task<FirebaseVisionText> result = detector.processImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        Log.d(TAG, "textDetector.onSuccess - firebaseVisionText: " + firebaseVisionText);
                        extractText(firebaseVisionText);
                        takePictureLock = false;
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "textDetector.onFailure: " + e.getMessage(), e);
                                takePictureLock = false;
                            }
                        });
    }

    private void extractText(FirebaseVisionText result) {
        String resultText = result.getText();
        Log.d(TAG, "extractText.resultText: " + resultText);

        for (FirebaseVisionText.TextBlock block : result.getTextBlocks()) {
            String blockText = block.getText();
            Log.d(TAG, "extractText.blockText: " + blockText);

//            Float blockConfidence = block.getConfidence();
//            List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
//            Point[] blockCornerPoints = block.getCornerPoints();
//            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionText.Line line : block.getLines()) {
                String lineText = line.getText();
                Log.d(TAG, "textDetector.lineText: " + lineText);

//                Float lineConfidence = line.getConfidence();
//                List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
//                Point[] lineCornerPoints = line.getCornerPoints();
//                Rect lineFrame = line.getBoundingBox();
                for (FirebaseVisionText.Element element : line.getElements()) {
                    String elementText = element.getText();
                    Log.d(TAG, "textDetector.elementText: " + elementText);

//                    Float elementConfidence = element.getConfidence();
//                    List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
//                    Point[] elementCornerPoints = element.getCornerPoints();
//                    Rect elementFrame = element.getBoundingBox();
                }
            }
        }
    }


}
