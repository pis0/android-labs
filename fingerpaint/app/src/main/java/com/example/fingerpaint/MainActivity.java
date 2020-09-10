package com.example.fingerpaint;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fingerpaint.view.BackButton;
import com.example.fingerpaint.view.DrawingView;
import com.example.fingerpaint.view.SignatureBtn;
import com.example.fingerpaint.view.SignatureLabel;
import com.example.fingerpaint.view.SignatureView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS = 1 << 0;

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private FrameLayout layout;
    private DrawingView dv;
    private SignatureView sv;
    private SignatureLabel sl;
    private SignatureBtn saveBtn;
    private SignatureBtn clearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);

        if (checkPermissions()) createView();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && checkPermissions()) {
                Log.d(TAG, "onRequestPermissionsResult - success");
                createView();
            } else {
                Log.e(TAG, "onRequestPermissionsResult - failed");
            }
        }
    }

    public void createView() {
        layout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL
        );
        layout.setLayoutParams(layoutparams);
        setContentView(layout);

        sv = new SignatureView(this);
        layout.addView(sv);

        sl = new SignatureLabel(this);
        layout.addView(sl);

        dv = new DrawingView(this);
        layout.addView(dv);

        BackButton backBtn = new BackButton(this);
        layout.addView(backBtn);

        clearBtn = new SignatureBtn(this, "clear", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clear!");
                dv.clear();
            }
        });
        layout.addView(clearBtn);

        saveBtn = new SignatureBtn(this, "save", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "save!");
                saveImage();
            }
        });
        layout.addView(saveBtn);


        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                saveBtn.setPivotX(0);
                saveBtn.setPivotY(0);
                saveBtn.setRotation(90);
                saveBtn.setX((float) (sv.getWidth() * 0.2));
                saveBtn.setY((float) (sv.getHeight() * 0.925) - saveBtn.getWidth());

                clearBtn.setPivotX(0);
                clearBtn.setPivotY(0);
                clearBtn.setRotation(90);
                clearBtn.setX(saveBtn.getX());
                clearBtn.setY(saveBtn.getY() - clearBtn.getWidth());

            }
        });
    }

    private void saveImage() {

        final int MAX_SIZE = 720;
        final int QUALITY = 90;

        int originalW = dv.bitmap.getWidth();
        int originalH = dv.bitmap.getHeight();

        Bitmap out = Bitmap.createScaledBitmap(
                dv.bitmap,
                (int) (((double) MAX_SIZE / (double) originalH) * (double) originalW),
                MAX_SIZE,
                true);

        if (out != null)
            Log.d(TAG, "Bitmap out - " + out.getWidth() + ", " + out.getHeight());

        Bitmap newBitmap = Bitmap.createBitmap(out.getHeight(), out.getWidth(), out.getConfig());
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);

        Matrix matrix = new Matrix();
        matrix.setTranslate(0, out.getWidth());
        matrix.preRotate(-90.0f);
        canvas.drawBitmap(out, matrix, null);

        // TODO to comment
        MediaStore.Images.Media.insertImage(
                getContentResolver(),
                newBitmap,
                "out",
                "out"
        );

//        String imageFileName = "JPEG_" + 123456 + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = null;
//        try {
//            image = File.createTempFile(
//                    imageFileName,
//                    ".jpg",
//                    storageDir //dir //
//            );
//        } catch (IOException e) {
//            Log.e(TAG, "saveImage error: " + e.getMessage(), e);
//            return;
//        }
//        Log.d(TAG, "image: " + image);
//        File file = new File(image.getAbsolutePath());
//        FileOutputStream fOut;
//        try {
//            fOut = new FileOutputStream(file);
//            newBitmap.compress(Bitmap.CompressFormat.PNG, QUALITY, fOut);
//            fOut.flush();
//            fOut.close();
//            dv.bitmap.recycle();
//            out.recycle();
//            newBitmap.recycle();
//        } catch (Exception e) {
//            Log.e(TAG, "saveImage error: " + e.getMessage(), e);
//            return;
//        }
//
//        Log.d(TAG, "saveImage success: " + file.getAbsolutePath());

        finish();

    }


}

