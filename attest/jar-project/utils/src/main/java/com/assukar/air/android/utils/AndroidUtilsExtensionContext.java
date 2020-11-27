package com.assukar.air.android.utils;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.assukar.air.android.utils.functions.GetSharedPreferencesUIDFunction;
import com.assukar.air.android.utils.functions.InitFunction;

import java.util.HashMap;
import java.util.Map;

public class AndroidUtilsExtensionContext extends FREContext {

    private static final String TAG = "AndroidUtils";

    @Override
    public void dispose() {
        Log.d(TAG,"Context disposed.");
    }

    @Override
    public Map<String, FREFunction> getFunctions() {

        Map<String, FREFunction> functions = new HashMap<String, FREFunction>();

        functions.put("init", new InitFunction());
        functions.put("getSharedPreferencesUID", new GetSharedPreferencesUIDFunction());

        return functions;
    }




}
