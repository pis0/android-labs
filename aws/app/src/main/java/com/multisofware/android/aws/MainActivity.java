package com.multisofware.android.aws;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    static private String TAG = MainActivity.class.getSimpleName();


    static int GALLERY_REQUEST_CODE = 1 << 0;
    static int MAX_SIZE = 480;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init();
        pickFromGallery();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {

                try {
                    Uri selectedImage = Objects.requireNonNull(data).getData();
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(Objects.requireNonNull(selectedImage), "r");
                    FileDescriptor fileDescriptor = Objects.requireNonNull(parcelFileDescriptor).getFileDescriptor();

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                    final int originalW = options.outWidth;
                    final int originalH = options.outHeight;
                    float scale = (float) MAX_SIZE / (float) originalH;
                    options.inScaled = true;
                    options.inSampleSize = resolveSampleSize(options, (int) (originalW * scale), MAX_SIZE);
                    options.inMutable = false;
                    options.inJustDecodeBounds = false;
                    Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                    parcelFileDescriptor.close();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                    byte[] byteArray = stream.toByteArray();
                    image.recycle();

                    ByteBuffer sourceImageBytes = ByteBuffer.wrap(byteArray);
                    final Image source = new Image().withBytes(sourceImageBytes);

                    final Image target = new Image().withBytes(sourceImageBytes);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            final AmazonRekognitionClient rekognitionClient = new AmazonRekognitionClient(new AWSCredentials() {
                                @Override
                                public String getAWSAccessKeyId() {
                                    return "AKIAIPMQA5JKUG37UOSA";
                                }

                                @Override
                                public String getAWSSecretKey() {
                                    return "2jO9KI9FnxwD9V2YOjH8iSGJzXx/PT5s0S4dL+4f";
                                }
                            });

                            Log.d(TAG, "rekognitionClient: " + rekognitionClient.toString());

                            @SuppressLint("StaticFieldLeak")
                            AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {
                                @Override
                                protected Void doInBackground(String... strings) {
                                    Log.d(TAG, "doInBackground");
                                    Float similarityThreshold = 90F;
                                    CompareFacesRequest request = new CompareFacesRequest()
                                            .withSourceImage(source)
                                            .withTargetImage(target)
                                            .withSimilarityThreshold(similarityThreshold);

                                    Log.d(TAG, "request: " + request.toString());

                                    CompareFacesResult compareFacesResult = rekognitionClient.compareFaces(request);
                                    Log.d(TAG, "compareFacesResult: " + compareFacesResult.toString());
                                    List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
                                    for (CompareFacesMatch match : faceDetails) {
                                        ComparedFace face = match.getFace();
                                        BoundingBox position = face.getBoundingBox();
                                        Log.d(TAG, "Face at " + position.getLeft().toString()
                                                + " " + position.getTop()
                                                + " matches with " + match.getSimilarity().toString()
                                                + "% confidence.");
                                    }
//                                    List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();
//                                    Log.d(TAG, "There was " + uncompared.size() + " face(s) that did not match");
                                    return null;
                                }
                            };
                            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                        }
                    });


                } catch (IOException e) {
                    Log.e(TAG, "error: " + e.getMessage(), e);
                }
            }
        }
    }


    private void pickFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

}