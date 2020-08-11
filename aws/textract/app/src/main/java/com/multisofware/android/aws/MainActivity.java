package com.multisofware.android.aws;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.amazonaws.services.textract.AmazonTextractClient;
import com.amazonaws.services.textract.model.AnalyzeDocumentRequest;
import com.amazonaws.services.textract.model.AnalyzeDocumentResult;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;
import com.amazonaws.services.textract.model.Relationship;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    static private String TAG = MainActivity.class.getSimpleName();

    static int GALLERY_REQUEST_CODE = 1 << 0;

    public static int MAX_SIZE = 480;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        loading.dismiss();
    }

    private ProgressDialog loading;
    private AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loading = new ProgressDialog(this);
        loading.setMessage("processing...");
        loading.setCancelable(false);
        loading.setInverseBackgroundForced(false);

        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        dialogBuilder.create();

        pickFromGallery();
    }

    private void pickFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

//    public static int resolveSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//        if (height > reqHeight || width > reqWidth) {
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//            while ((halfHeight / inSampleSize) >= reqHeight
//                    && (halfWidth / inSampleSize) >= reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//        return inSampleSize;
//    }

    private ByteBuffer createImage(Uri imageUri) throws IOException {

        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(Objects.requireNonNull(imageUri), "r");
        FileDescriptor fileDescriptor = Objects.requireNonNull(parcelFileDescriptor).getFileDescriptor();

        final BitmapFactory.Options options = new BitmapFactory.Options();

        // comment to do not resize picture
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
//        final int originalW = options.outWidth;
//        final int originalH = options.outHeight;
//        float scale = (float) MAX_SIZE / (float) originalH;
//        options.inScaled = true;
//        options.inSampleSize = resolveSampleSize(options, (int) (originalW * scale), MAX_SIZE);
//        options.inMutable = false;

        options.inJustDecodeBounds = false;
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        parcelFileDescriptor.close();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream); // quality 60 (default)
        byte[] byteArray = stream.toByteArray();
        image.recycle();

        ByteBuffer sourceImageBytes = ByteBuffer.wrap(byteArray);

        return sourceImageBytes;
    }


    private ByteBuffer source;
//    private Image target;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {

                try {
                    Uri selectedImageUri = Objects.requireNonNull(data).getData();

                    source = createImage(selectedImageUri);
                    if (source != null) {
                        Log.d(TAG, "source: " + source.toString());

                        processTextExtraction();

                    }
                } catch (IOException e) {
                    Log.e(TAG, "error: " + e.getMessage(), e);
                }

            }
        }
    }


    private void processTextExtraction() {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading.show();
            }
        });


        final AmazonTextractClient textractClient = new AmazonTextractClient(new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return getResources().getString(R.string.AWSAccessKeyId);
            }

            @Override
            public String getAWSSecretKey() {
                return getResources().getString(R.string.AWSSecretKey);
            }
        });
        Log.d(TAG, "textractClient: " + textractClient.toString());


        @SuppressLint("StaticFieldLeak")
        AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {

                try {
                    Log.d(TAG, "doInBackground");

                    // regular detection
                    DetectDocumentTextRequest request = new DetectDocumentTextRequest().withDocument(new Document().withBytes(source));
                    DetectDocumentTextResult result = textractClient.detectDocumentText(request);

                    // analyzed detection
//                    AnalyzeDocumentRequest request = new AnalyzeDocumentRequest().withFeatureTypes("TABLES", "FORMS").withDocument(new Document().withBytes(source));
//                    AnalyzeDocumentResult result = textractClient.analyzeDocument(request);

                    Log.d(TAG, "result.getBlocks().size: " + result.getBlocks().size());

                    for (Block block : result.getBlocks()) {
                        displayBlockInfo(block);
                    }
                    //dialogBuilder.setMessage(faceMatchResult);

                } catch (Exception e) {
                    Log.e(TAG, "processRecognition error:" + e.getMessage(), e);

                    dialogBuilder.setMessage("fail");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.hide();
                        dialogBuilder.show();
                    }
                });

                return null;

            }
        };


        task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

    }


    private void displayBlockInfo(Block block) {
        Log.d(TAG, "Block Id : " + block.getId());

//        if (block.getText() != null) Log.d(TAG, block.getText());
        if (block.getText() != null) Log.d(TAG, "    Detected text: " + block.getText());

        Log.d(TAG, "    Type: " + block.getBlockType());

        if (block.getBlockType().equals("PAGE") != true) {
            Log.d(TAG, "    Confidence: " + block.getConfidence().toString());
        }
        if (block.getBlockType().equals("CELL")) {
            Log.d(TAG, "    Cell information:");
            Log.d(TAG, "        Column: " + block.getColumnIndex());
            Log.d(TAG, "        Row: " + block.getRowIndex());
            Log.d(TAG, "        Column span: " + block.getColumnSpan());
            Log.d(TAG, "        Row span: " + block.getRowSpan());

        }

        Log.d(TAG, "    Relationships");
        List<Relationship> relationships = block.getRelationships();
        if (relationships != null) {
            for (Relationship relationship : relationships) {
                Log.d(TAG, "        Type: " + relationship.getType());
                Log.d(TAG, "        IDs: " + relationship.getIds().toString());
            }
        } else {
            Log.d(TAG, "        No related Blocks");
        }

        Log.d(TAG, "    Geometry");
        Log.d(TAG, "        Bounding Box: " + block.getGeometry().getBoundingBox().toString());
        Log.d(TAG, "        Polygon: " + block.getGeometry().getPolygon().toString());

        List<String> entityTypes = block.getEntityTypes();

        Log.d(TAG, "    Entity Types");
        if (entityTypes != null) {
            for (String entityType : entityTypes) {
                Log.d(TAG, "        Entity Type: " + entityType);
            }
        } else {
            Log.d(TAG, "        No entity type");
        }
        if (block.getPage() != null)
            Log.d(TAG, "    Page: " + block.getPage());

    }


}

