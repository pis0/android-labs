package com.assukar.air.android.utils;

import android.content.Context;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

public class AndroidUtilsExtension implements FREExtension {

    private static final String TAG = "AndroidUtils";

    public static FREContext extensionContext;
    public static Context appContext;

    @Override
    public void dispose() {

        Log.d(TAG, "Extension disposed.");

        appContext = null;
        extensionContext = null;
    }

    @Override
    public void initialize() {
        Log.d(TAG, "Extension initialized.");
    }

    @Override
    public FREContext createContext(String s) {
        return new AndroidUtilsExtensionContext();
    }


    public static void dispatch(String eventName, String data) {

        Log.d(TAG, "dispatch - eventName:" + eventName + ", data:" + data);

        extensionContext.dispatchStatusEventAsync(eventName, data);
    }


}
