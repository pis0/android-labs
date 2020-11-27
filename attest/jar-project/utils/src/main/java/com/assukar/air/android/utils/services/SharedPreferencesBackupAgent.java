package com.assukar.air.android.utils.services;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.assukar.air.android.utils.functions.GetSharedPreferencesUIDFunction;

import java.io.IOException;

public class SharedPreferencesBackupAgent extends BackupAgentHelper {

    private static final String TAG = "AndroidUtils";

    public static final String PREFS_BACKUP_KEY = "wzhfvlK1X1TfXdP458h8JuCT8mpvEVNi"; // random

    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, GetSharedPreferencesUIDFunction.PREFS_NAME);
        addHelper(PREFS_BACKUP_KEY, helper);

        Log.d(TAG, "onCreate - PREFS_NAME:" + GetSharedPreferencesUIDFunction.PREFS_NAME);
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        super.onBackup(oldState, data, newState);

        Log.d(TAG, "onBackup");
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        super.onRestore(data, appVersionCode, newState);

        Log.d(TAG, "onRestore");
    }
}