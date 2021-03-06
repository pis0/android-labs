package com.multisofware.mob.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class Camera extends AppCompatActivity
{


    private static final String TAG = Camera.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS = 1 << 0;
//    private static final int REQUEST_TAKE_PHOTO = 1 << 1;


    private static final String[] PERMISSIONS = new String[]{
            android.Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        dispatchTakePictureIntent();

        if (checkPermissions()) initCamera(); //dispatchTakePictureIntent();
        else ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                REQUEST_PERMISSIONS);

    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Log.d(TAG, "onActivityResult - requestCode: " + requestCode + ", resultCode: " + resultCode);
//
//        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
//            Log.d(TAG, "onActivityResult - REQUEST_TAKE_PHOTO - success: " + imageBitmap + ", " + currentPhotoPath);
//        }
//
//    }


//    private void dispatchTakePictureIntent() {
//
//        if (!checkCameraHardware(this)) {
//            Log.e(TAG, "checkCameraHardware - error: This device has no access to camera features.");
//            return;
//        }
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException e) {
//                Log.e(TAG, "dispatchTakePictureIntent error: " + e.getMessage());
//            }
//            if (photoFile != null) {
//                //Uri photoURI = Uri.fromFile(photoFile);
//                Uri photoURI = FileProvider.getUriForFile(this, "com.multisofware.mob.camera", photoFile);
//
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//            }
//        }
//
//    }


    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }


//    String currentPhotoPath;
//
//    private File createImageFile() throws IOException {
//
//        @SuppressLint("SimpleDateFormat")
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//        currentPhotoPath = image.getAbsolutePath();
//
//        return image;
//    }


//    private final static int MAX_DIMENSION = 1024;
//    private String saveToInternalStorage(Bitmap originalBmp, String dirName, String fileName, int quality) {
//        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        //Bitmap b= BitmapFactory.decodeFile(PATH_ORIGINAL_IMAGE);
//
//        int originalW = originalBmp.getWidth();
//        int originalH = originalBmp.getHeight();
//        //Bitmap out = Bitmap.createScaledBitmap(originalBmp, originalW * (MAX_DIMENSION / originalH), MAX_DIMENSION, true);
//        Bitmap out = Bitmap.createScaledBitmap(originalBmp, originalW , originalH, true);
//
//        File file = new File(dir, fileName + ".jpg");
//        FileOutputStream fOut;
//        try {
//            fOut = new FileOutputStream(file);
//            out.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
//            fOut.flush();
//            fOut.close();
//            originalBmp.recycle();
//            out.recycle();
//        } catch (Exception e) {
//            Log.e(TAG, "saveToInternalStorage - error: " + e.getMessage(), e);
//        }
//
//        return file.getAbsolutePath();
//    }


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


//    private void galleryAddPic(String imgPath) {
//
//        Log.d(TAG, "galleryAddPic");
//
////        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
////        File f = new File(imgPath);
////        Uri contentUri = Uri.fromFile(f);
////        mediaScanIntent.setData(contentUri);
////        sendBroadcast(mediaScanIntent);
//
//
//        MediaScannerConnection.scanFile(
//                getApplicationContext(),
//                new String[]{imgPath},
//                null,
//                new MediaScannerConnection.OnScanCompletedListener() {
//                    @Override
//                    public void onScanCompleted(String path, Uri uri) {
//                        Log.d(TAG, "file " + path + " was scanned successfully: " + uri);
//
////                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
////                        mediaScanIntent.setData(uri);
////                        sendBroadcast(mediaScanIntent);
//                    }
//                });
//    }


    //    private VideoView videoView;
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
                if(FLPreview == v) {
                    Log.d(TAG, "HERE");
                    Toast.makeText(getApplicationContext(), "I am clicked", Toast.LENGTH_SHORT).show();

                    camera.takePicture(null, null, pictureCallback);

                }
            }
        });
        FLPreview.addView(preview);

    }


    public static android.hardware.Camera getCameraInstance() {
        android.hardware.Camera camera = null;
        try {
            camera = android.hardware.Camera.open();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return camera;
    }


    private android.hardware.Camera.PictureCallback pictureCallback = new android.hardware.Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {


            Toast.makeText(getApplicationContext(), "onPictureTaken", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onPictureTaken - data:" + data);



//            File pictureFile = getApplicationContext().getOutputMediaFile(MEDIA_TYPE_IMAGE);
//            if (pictureFile == null){
//                Log.d(TAG, "Error creating media file, check storage permissions");
//                return;
//            }
//
//            try {
//                FileOutputStream fos = new FileOutputStream(pictureFile);
//                fos.write(data);
//                fos.close();
//            } catch (FileNotFoundException e) {
//                Log.d(TAG, "File not found: " + e.getMessage());
//            } catch (IOException e) {
//                Log.d(TAG, "Error accessing file: " + e.getMessage());
//            }
        }

    };

}
