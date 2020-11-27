package com.assukar.air.android.utils.functions;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.assukar.air.android.utils.AndroidUtilsExtension;
import com.assukar.air.android.utils.events.AndroidUtilsEvent;

import java.util.UUID;

public class GetSharedPreferencesUIDFunction implements FREFunction {

    private static final String TAG = "AndroidUtils";

    public static final String PREFS_NAME = "ANDROID_LIB_SHARED_PREFERENCES";
    private static final String SHARED_PREFERENCES_UID = "ANDROID_LIB_SHARED_PREFERENCES_UID";

    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {


        Log.v(TAG, "call");

        getSharedPreferencesUID();


        return null;
    }


    private String getNonce() {
        return UUID.randomUUID().toString();
    }

    private void getSharedPreferencesUID() {

        Log.v(TAG, "getSharedPreferencesUID");

        SharedPreferences setHardwareIdSettings = AndroidUtilsExtension.appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor setHardwareIdSettingsEditor = setHardwareIdSettings.edit();
        SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(final SharedPreferences prefs, final String key) {
                Log.d(TAG, "onSharedPreferenceChanged");
                new BackupManager(AndroidUtilsExtension.appContext).dataChanged();
            }

        };
        setHardwareIdSettings.registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
        String sharedPreferencesUID = setHardwareIdSettings.getString(SHARED_PREFERENCES_UID, null);
        if(sharedPreferencesUID == null)
        {
            Log.d(TAG, "updating hid...");

            sharedPreferencesUID = getNonce();
            setHardwareIdSettingsEditor.putString(SHARED_PREFERENCES_UID, sharedPreferencesUID);
            setHardwareIdSettingsEditor.apply();
        }

        Log.d(TAG, sharedPreferencesUID);

        AndroidUtilsExtension.dispatch(AndroidUtilsEvent.SUCCESS, "{\"sharedPreferencesUID\":\""+ sharedPreferencesUID +"\"}");

    }

}
