package com.multisofware.android.firebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.util.Log;

public class FirebaseVisionImages {

    private static final String TAG = FirebaseVisionImages.class.getSimpleName();

    private Context context;

    public FirebaseVisionImages(Context context) {
        this.context = context;
    }

    public void dispose() {

        if (imageToUpload != null) {
            imageToUpload.recycle();
            imageToUpload = null;
        }

        if (imagesToValidate != null)
            for (Bitmap bmp : imagesToValidate) {
                bmp.recycle();
                bmp = null;
            }

        if (rawBitmap != null) {
            rawBitmap.recycle();
            rawBitmap = null;
        }
    }


    private Bitmap rawBitmap;

    public void setBitmap(Bitmap rawBitmap) {
        Matrix matrix = new Matrix();
        this.rawBitmap = Bitmap.createBitmap(
                rawBitmap,
                0,
                rawBitmap.getHeight() / 3,
                rawBitmap.getWidth(),
                rawBitmap.getHeight() / 3,
                matrix,
                false);
        processImageToUpload();
        processImagesToValidate();
    }


    public Bitmap imageToUpload;

    private void processImageToUpload() {
        final int MAX_SIZE = 720;
        int originalW = rawBitmap.getWidth();
        int originalH = rawBitmap.getHeight();
        imageToUpload = Bitmap.createScaledBitmap(
                rawBitmap,
                (int) (((double) MAX_SIZE / (double) originalH) * (double) originalW),
                MAX_SIZE,
                false);
        if (imageToUpload != null)
            Log.d(TAG, "imageToUpload - " + imageToUpload.getWidth() + ", " + imageToUpload.getHeight());
    }


    public Bitmap[] imagesToValidate;

    private void processImagesToValidate() {

        imagesToValidate = new Bitmap[2];

        Matrix matrix = new Matrix();
        Bitmap temp =
//                applyColorMatrix(
                applySharpen(
                        Bitmap.createBitmap(
                                rawBitmap,
                                3 * (rawBitmap.getWidth() / 4),
                                0,
                                rawBitmap.getWidth() / 4,
                                rawBitmap.getHeight(),
                                matrix,
                                false)
                );
//        );

        imagesToValidate[0] = rotate(temp, 0.0f);
        imagesToValidate[1] = rotate(temp, 90.0f);

    }


    private Bitmap rotate(Bitmap bmp, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
    }


    private Bitmap applySharpen(Bitmap bmp) {

//    float[] matrix_blur = {
//      1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
//      1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
//      1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f};

        float[] matrix_sharpen = {
                0, -1, 0,
                -1, 5.333f, -1,
                0, -1, 0};

        Bitmap result = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        RenderScript renderScript = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(renderScript, bmp);
        Allocation output = Allocation.createFromBitmap(renderScript, result);
        ScriptIntrinsicConvolve3x3 convolution = ScriptIntrinsicConvolve3x3.create(renderScript, Element.U8_4(renderScript));
        convolution.setInput(input);
        convolution.setCoefficients(matrix_sharpen); // set matrix here
        convolution.forEach(output);
        output.copyTo(result);
        renderScript.destroy();

        return result;
    }


    private Bitmap applyColorMatrix(Bitmap bmp) {
        float c = 1.5f;
        float b = 1.0f;
        float s = 1.0f;

        float lumR = 0.3086f;
        float lumG = 0.6094f;
        float lumB = 0.0820f;
        float t = (1.0f - c) / 2.0f;
        float sr = (1.0f - s) * lumR;
        float sg = (1.0f - s) * lumG;
        float sb = (1.0f - s) * lumB;

        float[] colorTransform = {
                c * (sr + s), c * (sr), c * (sr), 0, 0,
                c * (sg), c * (sg + s), c * (sg), 0, 0,
                c * (sb), c * (sb), c * (sb + s), 0, 0,
                0, 0, 0, 1, 0,
                t + b, t + b, t + b, 0, 1,
        };

        ColorMatrix colorMatrix = new ColorMatrix();
//        colorMatrix.setSaturation(0);
//        colorMatrix.setRGB2YUV(); // red map
        colorMatrix.set(colorTransform); //Apply the Polaroid Color

        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);

        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(bmp, 0, 0, paint);
        canvas.setBitmap(null);

        return bmp;

    }

}
