package com.multisofware.android.firebase;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.multisofware.android.camera.CameraPreview;
import com.multisofware.android.view.BackButton;
import com.multisofware.android.view.qrcode.QRCodeMask;
import com.multisofware.android.view.tickets.TicketsCounter;
import com.multisofware.android.view.tickets.TicketsLabel;
import com.multisofware.android.view.tickets.TicketsMask;

import java.util.List;

public class FirebaseTest extends AppCompatActivity {

    private static final String TAG = FirebaseTest.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS = 1 << 0;

    private static final String[] PERMISSIONS = new String[]{
            android.Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    //TODO to review
    private BackButton backButton;
    //
    private TicketsMask ticketsMask;
    private TicketsLabel ticketsLabel;
    private TicketsCounter ticketsCounter;
    //
    private QRCodeMask qrCodeMask;


    private TextView toastText;
    private Toast toast;

    private void createToast(Context context) {
        toastText = new TextView(context);
        toastText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toastText.setTextColor(0xffffffff);
        toastText.setWidth(600);

        toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);

        //TODO to fix (tickets settings)
//        toastText.setHeight(600);
//        toastText.setPivotX(300);
//        toastText.setPivotY(300);
//        toastText.setY(0);
//        toastText.setX(-500);
//        toastText.setRotation(90);
//        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.START, 0, 0);


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
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);

        FirebaseApp.initializeApp(this);

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
            camera = android.hardware.Camera.open();
            //camera = openFrontFacingCamera();
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

                    //Toast.makeText(getApplicationContext(), "I am clicked", Toast.LENGTH_SHORT).show();
                    showToast("I am clicked");

                    camera.takePicture(null, null, pictureCallback);
                    takePictureLock = true;

                }
            }
        });

        FLPreview.addView(preview);


        //TODO to review
//        ticketsMask = new TicketsMask(this);
//        FLPreview.addView(ticketsMask);
//        ticketsLabel = new TicketsLabel(this);
//        FLPreview.addView(ticketsLabel);
//        ticketsCounter = new TicketsCounter(this);
//        FLPreview.addView(ticketsCounter);
        qrCodeMask = new QRCodeMask(this);
        FLPreview.addView(qrCodeMask);
        //
        backButton = new BackButton(this);
        FLPreview.addView(backButton);



    }


    private android.hardware.Camera.PictureCallback pictureCallback = new android.hardware.Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
//            Toast.makeText(getApplicationContext(), "onPictureTaken", Toast.LENGTH_SHORT).show();
            showToast("onPictureTaken");
            Log.d(TAG, "onPictureTaken - data:" + data);

            processData(data);
            camera.startPreview();

        }

    };

    private void processData(byte[] data) {
        try {
            barCodeDetector(480, data);
//            textDetector(2048, data);
//            faceDetector(640, data);
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }


    private FirebaseVisionImage createFirebaseVisionImage(int maxSize, byte[] data) {

        Bitmap originalBmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        int originalW = originalBmp.getWidth();
        int originalH = originalBmp.getHeight();

        Log.d(TAG, "processData - MAX_SIZE: " + maxSize + ", originalW: " + originalW + ", originalH: " + originalH);

        int newWidth = maxSize;
        int newHeight = (int) (((double) maxSize / (double) originalW) * (double) originalH);

        float scaleWidth = ((float) newWidth) / originalW;
        float scaleHeight = ((float) newHeight) / originalH;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postRotate(
                0.0f //90.0f
        );

        Bitmap bmpOut = Bitmap.createBitmap(originalBmp,
                0,
                0,
                originalW,
                originalH,
                matrix,
                false);

//        Bitmap bmpOut = Bitmap.createScaledBitmap(originalBmp,
//                maxSize,
//                (int) (((double) maxSize / (double) originalW) * (double) originalH),
//                false);


        FirebaseVisionImage firebaseVisionImage = null;
        if (bmpOut != null) {
            firebaseVisionImage = FirebaseVisionImage.fromBitmap(bmpOut);
            Log.d(TAG, "processData - bmpOut:" + bmpOut + " - " + bmpOut.getWidth() + ", " + bmpOut.getHeight());
            return firebaseVisionImage;
        }

        return null;
    }

    private void faceDetector(int maxSize, byte[] data) {

        Log.d(TAG, "faceDetector");

        FirebaseVisionImage firebaseVisionImage = createFirebaseVisionImage(maxSize, data);

        if (firebaseVisionImage == null) {
            Log.d(TAG, "faceDetector error: firebaseVisionImage is null");
            takePictureLock = false;
            return;
        }

        // High-accuracy landmark detection and face classification
//        FirebaseVisionFaceDetectorOptions highAccuracyOpts =
//                new FirebaseVisionFaceDetectorOptions.Builder()
//                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
//                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
//                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
//                        .build();

        // Real-time contour detection of multiple faces
        FirebaseVisionFaceDetectorOptions realTimeOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .enableTracking()
                        .build();


        FirebaseVisionFaceDetector faceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(realTimeOpts);


        Log.d(TAG, "faceDetector - getting results...");

        Task<List<FirebaseVisionFace>> result = faceDetector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(
                        new OnSuccessListener<List<FirebaseVisionFace>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionFace> faces) {
                                Log.d(TAG, "faceDetector success. - faces.length:" + faces.size());
                                extractFaceInfo(faces);
                                takePictureLock = false;
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "faceDetector failed - error:" + e.getMessage(), e);
                                takePictureLock = false;
                            }
                        });


    }


    private List<FirebaseVisionPoint> faceContour;
    private List<FirebaseVisionPoint> leftEyeContour;
    private List<FirebaseVisionPoint> rightEyeContour;
    private List<FirebaseVisionPoint> leftEyeBrowTopContour;
    private List<FirebaseVisionPoint> leftEyeBrowBottomContour;
    private List<FirebaseVisionPoint> rightEyeBrowTopContour;
    private List<FirebaseVisionPoint> rightEyeBrowBottomContour;
    private List<FirebaseVisionPoint> noseBridgeContour;
    private List<FirebaseVisionPoint> noseBottomContour;
    //    private FirebaseVisionFaceLandmark leftEar;
//    private FirebaseVisionFaceLandmark rightEar;
    private FirebaseVisionFaceLandmark leftCheek;
    private FirebaseVisionFaceLandmark rightCheek;
    private FirebaseVisionFaceLandmark leftEye;
    private FirebaseVisionFaceLandmark rightEye;
    private FirebaseVisionFaceLandmark noseBase;
    private FirebaseVisionFaceLandmark mouthLeft;
    private FirebaseVisionFaceLandmark mouthRight;
    private FirebaseVisionFaceLandmark mouthBottom;

    private Rect boundingBox;

    private float rY;
    private float rZ;

    private PointF getListDiff(List<FirebaseVisionPoint> list1, List<FirebaseVisionPoint> list2, PointF boundingBoxDiff) {
        int len = Math.min(list1.size(), list2.size());
        FirebaseVisionPoint p1;
        FirebaseVisionPoint p2;
        float pXDiff = 0;
        float pYDiff = 0;
        float finalXDiff = 0;
        float finalYDiff = 0;
        for (int i = 0; i < len; i++) {
            p1 = list1.get(i);
            p2 = list2.get(i);
            pXDiff = (p1.getX() - Math.abs(p1.getX() - p2.getX())) / p1.getX();
            pYDiff = (p1.getY() - Math.abs(p1.getY() - p2.getY())) / p1.getY();
            //Log.d(TAG, "compare: p1(" + p1.getX() + ", " + p1.getY() + ") - p2(" + p2.getX() + ", " + p2.getY() + ") - diff(" + pXDiff + ", " + pYDiff + ")");
            finalXDiff += pXDiff;
            finalYDiff += pYDiff;
        }
        PointF result = new PointF(
                Math.abs(((finalXDiff / len) * boundingBoxDiff.x) - boundingBoxDiff.x),
                Math.abs(((finalYDiff / len) * boundingBoxDiff.y) - boundingBoxDiff.y)
        );
        // Log.d(TAG, "compare - diff(" + result.x + ", " + result.y + ")");
        return result;
    }

    private PointF getDiff(FirebaseVisionPoint p1, FirebaseVisionPoint p2, PointF boundingBoxDiff) {
        float pXDiff = 0;
        float pYDiff = 0;
        pXDiff = (p1.getX() - Math.abs(p1.getX() - p2.getX())) / p1.getX();
        pYDiff = (p1.getY() - Math.abs(p1.getY() - p2.getY())) / p1.getY();
        PointF result = new PointF(
                Math.abs((pXDiff * boundingBoxDiff.x) - boundingBoxDiff.x),
                Math.abs((pYDiff * boundingBoxDiff.y) - boundingBoxDiff.y)
        );
        return result;
    }

    private boolean extractFaceInfoFlag = false;

    private void extractFaceInfo(List<FirebaseVisionFace> faces) {

        for (FirebaseVisionFace face : faces) {

            float currentRY = face.getHeadEulerAngleY();
            float currentRZ = face.getHeadEulerAngleZ();

            Rect currentBoundingBox = face.getBoundingBox();

            List<FirebaseVisionPoint> currentFaceContour = face.getContour(FirebaseVisionFaceContour.FACE).getPoints();
            List<FirebaseVisionPoint> currentLeftEyeContour = face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
            List<FirebaseVisionPoint> currentRightEyeContour = face.getContour(FirebaseVisionFaceContour.RIGHT_EYE).getPoints();
            List<FirebaseVisionPoint> currentLeftEyeBrowTopContour = face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP).getPoints();
            List<FirebaseVisionPoint> currentLeftEyeBrowBottomContour = face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM).getPoints();
            List<FirebaseVisionPoint> currentRightEyeBrowTopContour = face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP).getPoints();
            List<FirebaseVisionPoint> currentRightEyeBrowBottomContour = face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM).getPoints();
            List<FirebaseVisionPoint> currentNoseBridgeContour = face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE).getPoints();
            List<FirebaseVisionPoint> currentNoseBottomContour = face.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM).getPoints();

//            FirebaseVisionFaceLandmark currentLeftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
//            FirebaseVisionFaceLandmark currentRightEar = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR);
            FirebaseVisionFaceLandmark currentLeftCheek = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK);
            FirebaseVisionFaceLandmark currentRightCheek = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK);
            FirebaseVisionFaceLandmark currentLeftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE);
            FirebaseVisionFaceLandmark currentRightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE);
            FirebaseVisionFaceLandmark currentNosebase = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE);
            FirebaseVisionFaceLandmark currentMouthLeft = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT);
            FirebaseVisionFaceLandmark currentMouthRight = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT);
            FirebaseVisionFaceLandmark currentMouthBottom = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM);

            // contour
            if (faceContour == null)
                faceContour = currentFaceContour;
            if (leftEyeContour == null)
                leftEyeContour = currentLeftEyeContour;
            if (rightEyeContour == null)
                rightEyeContour = currentRightEyeContour;
            if (leftEyeBrowTopContour == null)
                leftEyeBrowTopContour = currentLeftEyeBrowTopContour;
            if (leftEyeBrowBottomContour == null)
                leftEyeBrowBottomContour = currentLeftEyeBrowBottomContour;
            if (rightEyeBrowTopContour == null)
                rightEyeBrowTopContour = currentRightEyeBrowTopContour;
            if (rightEyeBrowBottomContour == null)
                rightEyeBrowBottomContour = currentRightEyeBrowBottomContour;
            if (noseBridgeContour == null)
                noseBridgeContour = currentNoseBridgeContour;
            if (noseBottomContour == null)
                noseBottomContour = currentNoseBottomContour;

            // landmark
//            if (leftEar == null)
//                leftEar = currentLeftEar;
//            if (rightEar == null)
//                rightEar = currentRightEar;
            if (leftCheek == null)
                leftCheek = currentLeftCheek;
            if (rightCheek == null)
                rightCheek = currentRightCheek;
            if (leftEye == null)
                leftEye = currentLeftEye;
            if (rightEye == null)
                rightEye = currentRightEye;
            if (noseBase == null)
                noseBase = currentNosebase;
            if (mouthLeft == null)
                mouthLeft = currentMouthLeft;
            if (mouthRight == null)
                mouthRight = currentMouthRight;
            if (mouthBottom == null)
                mouthBottom = currentMouthBottom;


            // boundingBox
            if (boundingBox == null)
                boundingBox = currentBoundingBox;

            // rotation
            if (rY == 0) rY = currentRY;
            if (rZ == 0) rZ = currentRZ;

            Log.d(TAG, "currentRY " + currentRY);
            Log.d(TAG, "currentRZ " + currentRZ);
            float rYDiff = ((rY - Math.abs(rY - currentRY)) / rY) / 360;
            float rZDiff = ((rZ - Math.abs(rZ - currentRZ)) / rZ) / 360;
            Log.d(TAG, "rotation compare. " + rYDiff + ", " + rZDiff);

            float bw = boundingBox.width();
            float bh = boundingBox.height();
            float cbw = currentBoundingBox.width();
            float cbh = currentBoundingBox.height();
            PointF boundingBoxDiff = new PointF(
                    ((float) (bw - Math.abs(bw - cbw)) / bw) + rYDiff,
                    ((float) (bh - Math.abs(bh - cbh)) / bh) + rZDiff
            );
            Log.d(TAG, "boundingBox compare. " + boundingBoxDiff);


            PointF faceContourDiff;
            faceContourDiff = getListDiff(faceContour, currentFaceContour, boundingBoxDiff);
            Log.d(TAG, "faceContour compare. " + faceContourDiff);

            PointF leftEyeContourDiff;
            leftEyeContourDiff = getListDiff(leftEyeContour, currentLeftEyeContour, boundingBoxDiff);
            Log.d(TAG, "leftEyeContour compare. " + leftEyeContourDiff);

            PointF rightEyeContourDiff;
            rightEyeContourDiff = getListDiff(rightEyeContour, currentRightEyeContour, boundingBoxDiff);
            Log.d(TAG, "rightEyeContour compare. " + rightEyeContourDiff);

            PointF leftEyeBrowTopContourDiff;
            leftEyeBrowTopContourDiff = getListDiff(leftEyeBrowTopContour, currentLeftEyeBrowTopContour, boundingBoxDiff);
            Log.d(TAG, "leftEyeBrowTopContour compare. " + leftEyeBrowTopContourDiff);

            PointF leftEyeBrowBottomContourDiff;
            leftEyeBrowBottomContourDiff = getListDiff(leftEyeBrowBottomContour, currentLeftEyeBrowBottomContour, boundingBoxDiff);
            Log.d(TAG, "leftEyeBrowBottomContour compare. " + leftEyeBrowBottomContourDiff);

            PointF rightEyeBrowTopContourDiff;
            rightEyeBrowTopContourDiff = getListDiff(rightEyeBrowTopContour, currentRightEyeBrowTopContour, boundingBoxDiff);
            Log.d(TAG, "rightEyeBrowTopContour compare. " + rightEyeBrowTopContourDiff);

            PointF rightEyeBrowBottomContourDiff;
            rightEyeBrowBottomContourDiff = getListDiff(rightEyeBrowBottomContour, currentRightEyeBrowBottomContour, boundingBoxDiff);
            Log.d(TAG, "rightEyeBrowBottomContour compare. " + rightEyeBrowBottomContourDiff);

            PointF noseBridgeContourDiff;
            noseBridgeContourDiff = getListDiff(noseBridgeContour, currentNoseBridgeContour, boundingBoxDiff);
            Log.d(TAG, "noseBridgeContour compare. " + noseBridgeContourDiff);

            PointF noseBottomContourDiff;
            noseBottomContourDiff = getListDiff(noseBottomContour, currentNoseBottomContour, boundingBoxDiff);
            Log.d(TAG, "noseBottomContour compare. " + noseBottomContourDiff);

//            PointF leftEarDiff;
//            if (leftEar != null && currentLeftEar != null) {
//                leftEarDiff = getDiff(leftEar.getPosition(), currentLeftEar.getPosition(), boundingBoxDiff);
//                Log.d(TAG, "leftEarDiff compare. " + leftEarDiff);
//            }
//
//            PointF rightEarDiff;
//            if (rightEar != null && currentRightEar != null) {
//                rightEarDiff = getDiff(rightEar.getPosition(), currentRightEar.getPosition(), boundingBoxDiff);
//                Log.d(TAG, "rightEar compare. " + rightEarDiff);
//            }

            PointF leftCheekDiff = null;
            if (leftCheek != null && currentLeftCheek != null) {
                leftCheekDiff = getDiff(leftCheek.getPosition(), currentLeftCheek.getPosition(), boundingBoxDiff);
                Log.d(TAG, "leftCheek compare. " + leftCheekDiff);
            }

            PointF rightCheekDiff = null;
            if (rightCheek != null && currentRightCheek != null) {
                rightCheekDiff = getDiff(rightCheek.getPosition(), currentRightCheek.getPosition(), boundingBoxDiff);
                Log.d(TAG, "rightCheek compare. " + rightCheekDiff);
            }

            PointF leftEyeDiff = null;
            if (leftEye != null && currentLeftEye != null) {
                leftEyeDiff = getDiff(leftEye.getPosition(), currentLeftEye.getPosition(), boundingBoxDiff);
                Log.d(TAG, "leftEye compare. " + leftEyeDiff);
            }

            PointF rightEyeDiff = null;
            if (rightEye != null && currentRightEye != null) {
                rightEyeDiff = getDiff(rightEye.getPosition(), currentRightEye.getPosition(), boundingBoxDiff);
                Log.d(TAG, "rightEye compare. " + rightEyeDiff);
            }

            PointF noseBaseDiff = null;
            if (noseBase != null && currentNosebase != null) {
                noseBaseDiff = getDiff(noseBase.getPosition(), currentNosebase.getPosition(), boundingBoxDiff);
                Log.d(TAG, "noseBase compare. " + noseBaseDiff);
            }

            PointF mouthLeftDiff = null;
            if (mouthLeft != null && currentMouthLeft != null) {
                mouthLeftDiff = getDiff(mouthLeft.getPosition(), currentMouthLeft.getPosition(), boundingBoxDiff);
                Log.d(TAG, "mouthLeft compare. " + mouthLeftDiff);
            }

            PointF mouthRightDiff = null;
            if (mouthRight != null && currentMouthRight != null) {
                mouthRightDiff = getDiff(mouthRight.getPosition(), currentMouthRight.getPosition(), boundingBoxDiff);
                Log.d(TAG, "mouthRight compare. " + mouthRightDiff);
            }

            PointF mouthBottomDiff = null;
            if (mouthBottom != null && currentMouthBottom != null) {
                mouthBottomDiff = getDiff(mouthBottom.getPosition(), currentMouthBottom.getPosition(), boundingBoxDiff);
                Log.d(TAG, "mouthBottom compare. " + mouthBottomDiff);
            }


            PointF[] temp = new PointF[]{
                    faceContourDiff,
                    leftEyeContourDiff,
                    rightEyeContourDiff,
                    leftEyeBrowTopContourDiff,
                    leftEyeBrowBottomContourDiff,
                    rightEyeBrowTopContourDiff,
                    rightEyeBrowBottomContourDiff,
                    noseBridgeContourDiff,
                    noseBottomContourDiff,
//                    leftEarDiff,
//                    rightEarDiff,
                    leftCheekDiff,
                    rightCheekDiff,
                    leftEyeDiff,
                    rightEyeDiff,
                    noseBaseDiff,
                    mouthLeftDiff,
                    mouthRightDiff,
                    mouthBottomDiff
            };

            int errorCount = 0;
            float valueToCompare = 0.07f;
            for (PointF pf : temp) {
                if (pf.x >= valueToCompare) errorCount++;
                if (pf.y >= valueToCompare) errorCount++;
            }

//            if (errorCount >= 2) {
//            Toast.makeText(getApplicationContext(), "errorCount: " + errorCount, Toast.LENGTH_SHORT).show();
            showToast("errorCount: " + errorCount);
//            } else if(extractFaceInfoFlag) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//                alertDialogBuilder.setMessage("RECONHECEU!");
//                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
//                {
//                    public void onClick(DialogInterface dialog, int whichButton)
//                    {
//                        finish();
//                    }
//                });
//                alertDialogBuilder.create().show();
//            }
        }

        extractFaceInfoFlag = true;
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