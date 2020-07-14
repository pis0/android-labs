package com.multisofware.android.firebase;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.multisofware.android.view.BackButton;
import com.multisofware.android.view.Output;
import com.multisofware.android.view.qrcode.QRCodeLabel;
import com.multisofware.android.view.qrcode.QRCodeMask;
import com.multisofware.android.view.tickets.TicketsCounter;
import com.multisofware.android.view.tickets.TicketsLabel;
import com.multisofware.android.view.tickets.TicketsMask;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Audio;
import com.otaliastudios.cameraview.controls.Engine;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.size.Size;
import com.otaliastudios.cameraview.size.SizeSelector;

import java.io.IOException;
import java.util.ArrayList;
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
    private QRCodeLabel qrCodeLabel;


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
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE;
//        decorView.setSystemUiVisibility(uiOptions);

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
//            camera = android.hardware.Camera.open();
            camera = openFrontFacingCamera();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return camera;
    }

    //    private android.hardware.Camera camera;
    //private CameraPreview preview;
    private CameraView cameraView;
    private FrameLayout FLPreview;

    private Boolean takePictureLock = false;

    private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
        FirebaseVisionImageMetadata imageMetaData = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_YV12)
                .setRotation(FirebaseVisionImageMetadata.ROTATION_270)
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
                .build();

        byte[] frameBytes = frame.getData();

        return FirebaseVisionImage.fromByteArray(frameBytes, imageMetaData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cameraView.clearFrameProcessors();
        cameraView.destroy();

    }

    private void initCamera() {

        if (!checkCameraHardware(this)) {
            Log.e(TAG, "initCamera error: This device has no access to camera features");
            return;
        }

//        camera = getCameraInstance();

        FLPreview = (FrameLayout) findViewById(R.id.camera_preview);

        //preview = new CameraPreview(this, camera);
        cameraView = new CameraView(this);
        cameraView.setLifecycleOwner(this);

        cameraView.setEngine(Engine.CAMERA2);
        cameraView.setFacing(Facing.FRONT);
        cameraView.setAudio(Audio.OFF);
        cameraView.setFlash(Flash.OFF);
        cameraView.setPreviewStreamSize(new SizeSelector() {
            @NonNull
            @Override
            public List<Size> select(@NonNull List<Size> source) {

                for (Size size : source) {
                    Log.d(TAG, "getSupportedPictureSizes: " + size.getWidth() + ", " + size.getHeight());
                }

                List<Size> list = new ArrayList<>(1);
                Size size = new Size(480, 640);
                list.add(size);

                return list;
            }
        });

        FLPreview.addView(cameraView);


        final Output output = new Output(this);
        FLPreview.addView(output);


        FirebaseVisionFaceDetectorOptions realTimeOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
//                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
//                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .enableTracking()
                        .build();

        final FirebaseVisionFaceDetector faceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(realTimeOpts);

        cameraView.addFrameProcessor(new FrameProcessor() {

            private boolean lock = false;
            private int prevTrakingId = -1;
            private int blinkCounter = 0;
            private int notBlinkCounter = 0;

            @Override
            public void process(@NonNull final Frame frame) {

                if (lock) return;
                lock = true;

                final FirebaseVisionImage image = getVisionImageFromFrame(frame);

                faceDetector.detectInImage(image).addOnSuccessListener(
                        new OnSuccessListener<List<FirebaseVisionFace>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionFace> faces) {
                                //Log.d(TAG, "faceDetector success. - faces.length:" + faces.size());

                                String result = "";
                                result += "faces: " + faces.size() + ", " + cameraView.getRotation();

//                                if (faces.size() == 0) {
//                                    blinkCounter = 0;
//                                    notBlinkCounter = 0;
//                                }

                                for (FirebaseVisionFace face : faces) {

                                    Rect bounds = face.getBoundingBox();
                                    result += "\nbounds: " + bounds.toShortString();

                                    float rotY = face.getHeadEulerAngleY();
                                    float rotZ = face.getHeadEulerAngleZ();
                                    result += "\nrotY: " + rotY + ", rotZ: " + rotZ;

                                    FirebaseVisionFaceLandmark leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE);
                                    FirebaseVisionFaceLandmark rightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE);
                                    if (leftEye != null && rightEye != null) {
                                        FirebaseVisionPoint leftEyePos = leftEye.getPosition();
                                        FirebaseVisionPoint rightEyePos = rightEye.getPosition();
                                        result += "\nleftEyePos: " + leftEyePos.toString() + ", rightEyePos: " + rightEyePos.toString();
                                    }

                                    result += "\nsmileProb: " + face.getSmilingProbability();

                                    if (face.getLeftEyeOpenProbability() < 0.4 && face.getRightEyeOpenProbability() < 0.4) {
                                        result += "\nblinking - " + blinkCounter++;
                                        notBlinkCounter = 0;
                                    } else {
                                        result += "\nnot blinking" + notBlinkCounter++;
                                    }


                                    if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                        int id = face.getTrackingId();
                                        result += "\ntrackingId: " + id;
                                        if (prevTrakingId != id) {
                                            prevTrakingId = id;
                                            blinkCounter = 0;
                                            notBlinkCounter = 0;
                                        }
                                    }
                                }


                                if (blinkCounter >= 1 && notBlinkCounter >= 1) {
                                    Bitmap bmp = image.getBitmap();
                                    output.setText("take picture! " + bmp);

                                    try {
                                        faceDetector.close();
                                        cameraView.clearFrameProcessors();

                                        createImageFile("", bmp);

                                    } catch (IOException ignored) {
                                    }


                                } else {
                                    output.setText(result);
                                    lock = false;
                                }


                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "  failed - error:" + e.getMessage(), e);

//                                        try {
//                                            faceDetector.close();
                                        lock = false;
//                                        } catch (IOException ignored) {
//                                        }
                                    }
                                });

            }
        });


        //preview = findViewById(R.id.cameraView);
        //preview.setLifecycleOwner(this);

//        FLPreview.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (FLPreview == v && !takePictureLock) {
//
//                    //TODO to review
////                    preview.autoFocus(new IAutoFocusCallback() {
////                        @Override
////                        public void run(Boolean success) {
////                            if(success) {
//                    showToast("I am clicked");
//                    camera.takePicture(null, null, pictureCallback);
//                    takePictureLock = true;
////                            }
////                        }
////                    });
//
//                }
//            }
//        });


        //TODO to review
////        ticketsMask = new TicketsMask(this);
////        FLPreview.addView(ticketsMask);
////        ticketsLabel = new TicketsLabel(this);
////        FLPreview.addView(ticketsLabel);
////        ticketsCounter = new TicketsCounter(this);
////        FLPreview.addView(ticketsCounter);
//        qrCodeMask = new QRCodeMask(this);
//        FLPreview.addView(qrCodeMask);
//        qrCodeLabel = new QRCodeLabel(this);
//        FLPreview.addView(qrCodeLabel);

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

//            processData(data);
            camera.startPreview();

        }

    };

    private void processData(byte[] data) {
        try {
//            barCodeDetector(480, data);
//            textDetector(2048, data);
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }

    private int resolveSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private FirebaseVisionImage createFirebaseVisionImage(int maxSize, byte[] data) {

//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeByteArray(data, 0, data.length, options);
//        int originalW = options.outWidth;
//        int originalH = options.outHeight;
//
//        Log.d(TAG, "processData - MAX_SIZE: " + maxSize + ", originalW: " + originalW + ", originalH: " + originalH);
//
//        //float scale = (float) ((double) maxSize / (double) originalH);
//        Matrix matrix = new Matrix();
//        matrix.postRotate(
//                90.0f
//        );

        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options1);
        final int originalW1 = options1.outWidth;
        final int originalH1 = options1.outHeight;

        BitmapFactory.Options options2 = new BitmapFactory.Options();
        float scale = (float) maxSize / (float) originalH1;
        options2.inSampleSize = resolveSampleSize(options2, (int) (originalW1 * scale), maxSize);
        options2.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options2);
        final int originalW2 = options2.outWidth;
        final int originalH2 = options2.outHeight;

        Bitmap originalBmp = null;
        try {

            BitmapRegionDecoder rDecoder = BitmapRegionDecoder.newInstance(
                    data,
                    0,
                    data.length,
                    false
            );
            originalBmp = rDecoder.decodeRegion(
                    new Rect((originalW2 / 3) * 2, originalH2 / 3, (originalW2 / 3) * 3, (originalH2 / 3) * 2),
                    options2
            );
            rDecoder.recycle();
        } catch (Exception e) {
            Log.e(TAG, "processData error: " + e.getMessage(), e);
        }


        //TODO to implement crop
//        FirebaseVisionImages firebaseVisionImages = new FirebaseVisionImages(this);
//        firebaseVisionImages.setBitmap(originalBmp);


        try {

            if (originalBmp != null) {
                Log.d(TAG, "processData - outWidth: " + originalBmp.getWidth() + ", outHeight: " + originalBmp.getHeight() + ", density: " + originalBmp.getDensity());
                createImageFile("temp", originalBmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
//        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(firebaseVisionImages.imagesToValidate[1]);
//        Log.d(TAG, "processData - result:" + firebaseVisionImages.imagesToValidate[1] + " - " + firebaseVisionImages.imagesToValidate[1].getWidth() + ", " + firebaseVisionImages.imagesToValidate[1].getHeight());
//        return firebaseVisionImage;


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


        final FirebaseVisionImage firebaseVisionImage = createFirebaseVisionImage(maxSize, data);

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


    private void createImageFile(String ticketNumber, Bitmap original) throws IOException {

        Log.d(TAG, "createImageFile - Bitmap original - " + original.getWidth() + ", " + original.getHeight());


        // Save image to gallery
        String savedImageURL = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                original,
                "temp",
                "temp"
        );

//        final int MAX_SIZE = 720;
//        final int QUALITY = 90;
//
//        int originalW = original.getWidth();
//        int originalH = original.getHeight();
//
//        Bitmap out = Bitmap.createScaledBitmap(original,
//                (int) (((double) MAX_SIZE / (double) originalH) * (double) originalW),
//                MAX_SIZE,
//                false);
//
//        if (out != null)
//            Log.d(TAG, "Bitmap out - " + out.getWidth() + ", " + out.getHeight());
//
//        //@SuppressLint("SimpleDateFormat")
//        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//
//        String imageFileName = "JPEG_" + ticketNumber + "_"; //+ timeStamp + "_";
////        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/temp";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
////        File dir = new File(Environment.getExternalStorageDirectory().toString());
//
//
//
////        if (!dir.exists()) {
////            boolean created = dir.mkdir();
////            Log.d(TAG, "dir: " + dir + ", created: " + created);
////        }
//
//        File image = File.createTempFile(
//                imageFileName,
//                ".jpg",
//                storageDir //dir //
//        );
//
//        Log.d(TAG, "image: " + image);
//
//
//
//        File file = new File(image.getAbsolutePath());
//        FileOutputStream fOut;
//        try {
//            fOut = new FileOutputStream(file);
//            out.compress(Bitmap.CompressFormat.PNG, QUALITY, fOut);
//            fOut.flush();
//            fOut.close();
//            original.recycle();
//            out.recycle();
//        } catch (Exception e) {
//            Log.e(TAG, "onActivityResult - REQUEST_TAKE_PHOTO - error: " + e.getMessage(), e);
//        }
//
//        addImageToGallery(file.getAbsolutePath(), this);

    }


}
