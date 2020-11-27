package com.assukar.air.android.attest;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.assukar.air.android.attest.functions.AttestFunction;
import com.assukar.air.android.attest.functions.InitFunction;

import java.util.HashMap;
import java.util.Map;

public class AndroidAttestExtensionContext extends FREContext {

    private static final String TAG = "AndroidUtils";

    @Override
    public void dispose() {
        Log.d(TAG,"Context disposed.");
    }

    @Override
    public Map<String, FREFunction> getFunctions() {

        Map<String, FREFunction> functions = new HashMap<String, FREFunction>();

        functions.put("init", new InitFunction());
        functions.put("attest", new AttestFunction());

        return functions;
    }




}
