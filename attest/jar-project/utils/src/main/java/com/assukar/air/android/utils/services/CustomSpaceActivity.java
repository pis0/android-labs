package com.assukar.air.android.utils.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

@SuppressLint("Registered")
public class CustomSpaceActivity extends Activity {

    private static final String TAG = "AndroidUtils";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "CustomSpaceActivity - onCreate");

        //to do nothing
        finish();

    }
}
