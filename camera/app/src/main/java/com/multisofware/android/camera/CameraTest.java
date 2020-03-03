package com.multisofware.android.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraTest extends AppCompatActivity {


    private static final String TAG = CameraTest.class.getSimpleName();
    //    private static final int REQUEST_CAMERA_PERMISSION = 1 << 0;
    //    private static final int REQUEST_IMAGE_CAPTURE = 1 << 1;
    private static final int REQUEST_TAKE_PHOTO = 1 << 2;
//    private VideoView videoView;


    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dispatchTakePictureIntent();


//        if (checkPermission()) dispatchTakePictureIntent();
//        else ActivityCompat.requestPermissions(
//                this,
//                new String[]{android.Manifest.permission.CAMERA},
//                REQUEST_CAMERA_PERMISSION);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            assert extras != null;
            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            Log.d(TAG, "onActivityResult - REQUEST_TAKE_PHOTO - success: " + imageBitmap + ", " + currentPhotoPath);
//            galleryAddPic();
        }

        Log.d(TAG, "onActivityResult - requestCode: " + requestCode + ", resultCode: " + resultCode);

//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//
//            Log.d(TAG, "onActivityResult - imageBitmap:" + imageBitmap);
//        }

//        if (requestCode == REQUEST_CAMERA_PERMISSION && resultCode == RESULT_OK) {
//
//            Log.d(TAG, "onActivityResult - REQUEST_CAMERA_PERMISSION - success: " + currentPhotoPath);
//            dispatchTakePictureIntent();
//
//        } else


    }


    private void dispatchTakePictureIntent() {

        if (!checkCameraHardware(this)) {
            Log.e(TAG, "checkCameraHardware - error: This device has no access to camera features.");
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e(TAG, "createImageFile error: " + e.getMessage());
                return;
            }

            Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
                    "com.multisofware.android.camera",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }


    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private String currentPhotoPath;

    private File createImageFile() throws IOException {

        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }


    //    private void galleryAddPic() {
////        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
////        File f = new File(currentPhotoPath);
////        Uri contentUri = Uri.fromFile(f);
////        mediaScanIntent.setData(contentUri);
////        this.sendBroadcast(mediaScanIntent);
//
//        Log.d(TAG, "galleryAddPic");
//
//        MediaScannerConnection.scanFile(
//                getApplicationContext(),
//                new String[]{currentPhotoPath},
//                null,
//                new MediaScannerConnection.OnScanCompletedListener() {
//                    @Override
//                    public void onScanCompleted(String path, Uri uri) {
//                        Log.v(TAG, "file " + path + " was scanned seccessfully: " + uri);
//
//                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                        mediaScanIntent.setData(uri);
//                        sendBroadcast(mediaScanIntent);
//                    }
//                });
//    }


//    private boolean checkPermission() {
//        return ContextCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED;
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CAMERA_PERMISSION: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    Log.e(TAG, "onRequestPermissionsResult - success");
//                    dispatchTakePictureIntent();
//
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request.
//        }
//    }


//    private void initCamera() {
//
//        if(!checkCameraHardware(this)) {
//            Log.e(TAG, "initCamera error: This device has no access to camera features");
//            return;
//        }
//
//        // Create an instance of Camera
//        mCamera = getCameraInstance();
//
//        // Create our Preview view and set it as the content of our activity.
//        mPreview = new CameraPreview(this, mCamera);
//        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
//        preview.addView(mPreview);
//    }


//    public static Camera getCameraInstance() {
//        Camera camera = null;
//        try {
//            camera = Camera.open();
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage(), e);
//        }
//        return camera;
//    }


//    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
//        @Override
//        public void onPictureTaken(byte[] data, Camera camera) {
//            Log.d(TAG, "onPictureTaken: ");
////            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
////            if (pictureFile == null){
////                Log.d(TAG, "Error creating media file, check storage permissions");
////                return;
////            }
////            try {
////                FileOutputStream fos = new FileOutputStream(pictureFile);
////                fos.write(data);
////                fos.close();
////            } catch (FileNotFoundException e) {
////                Log.d(TAG, "File not found: " + e.getMessage());
////            } catch (IOException e) {
////                Log.d(TAG, "Error accessing file: " + e.getMessage());
////            }
//        }
//    };


}
