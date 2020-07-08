package com.multisofware.android.camera;

import android.os.AsyncTask;

public class AutoFocusCallback extends AsyncTask<String, Void, Boolean> {

    final IAutoFocusCallback callback;

    AutoFocusCallback(IAutoFocusCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        callback.run(aBoolean);
    }
}