package com.multisofware.android.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CameraPreview.class.getSimpleName();

    private SurfaceHolder mHolder;
    private Camera mCamera;



    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Error setting camera preview: " + e.getMessage(), e);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }


    private Camera.Size resolveCameraSize(Camera.Parameters params) {
        Camera.Size previewSize = params.getPreviewSize();
        Log.d(TAG, "resolveCameraSize - previewSize: " + previewSize.width + ", " + previewSize.height);
        float previewRatio = ((float) previewSize.width) / ((float)previewSize.height);
//        float closerWidth = 0;
        float closerDiff = Integer.MAX_VALUE;
        float currentDiff = 0;

//        for (Camera.Size size : params.getSupportedPictureSizes()) {
//            Log.d(TAG, "resolveCameraSize - getSupportedPictureSizes: " + size.width + ", " + size.height);
//            currentDiff = Math.abs(size.width - previewSize.width);
//            if (currentDiff < closerDiff) {
//                closerDiff = currentDiff;
//                closerWidth = size.width;
//            }
//        }
//        ArrayList<Camera.Size> validSizes = new ArrayList<>();
//        for (Camera.Size size : params.getSupportedPictureSizes()) {
//            if (closerWidth == size.width) validSizes.add(size);
//        }

        Camera.Size closerSize = null;

//        closerDiff = Integer.MAX_VALUE;
//        for (Camera.Size size : validSizes) {
        for (Camera.Size size : params.getSupportedPictureSizes()) {
            float currentRatio = ((float) size.width) / ((float)size.height);
            currentDiff = Math.abs(currentRatio - previewRatio);
            Log.d(TAG, "resolveCameraSize - getSupportedPictureSizes: " + size.width + ", " + size.height + ", " + currentRatio + ", " + currentDiff);
            if (currentDiff < closerDiff) {
                closerDiff = currentDiff;
                closerSize = size;
            }
        }
        Log.d(TAG, "resolveCameraSize - closeSize: " + (closerSize != null ? closerSize.width + "x" + closerSize.height : "null"));

        return closerSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null) return;

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.e(TAG, "surfaceChanged error: " + e.getMessage(), e);
        }

        try {
            // set preview size and make any resize, rotate or
            // reformatting changes here
            mCamera.setDisplayOrientation(90);
            Camera.Parameters params = mCamera.getParameters();

            //TODO to review
            Camera.Size camSize = resolveCameraSize(params);
            params.setPictureSize(camSize.width, camSize.height);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            mCamera.setParameters(params);
        } catch (Exception e) {
            Log.e(TAG, "surfaceChanged error: " + e.getMessage(), e);
        }


        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "surfaceChanged error: " + e.getMessage(), e);
        }

    }
}