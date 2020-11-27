package com.assukar.air.android.attest.functions;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.assukar.air.android.attest.AndroidAttestExtension;
import com.assukar.air.android.attest.actions.AndroidAttestActions;

public class InitFunction implements FREFunction {

    private static final String TAG = "AndroidAttest";

    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        Log.v(TAG, "InitFunction - call");
        initContext(freContext);

        return null;
    }


    private void initContext(FREContext freContext) {

        Log.v(TAG, "InitFunction - initContext");

        AndroidAttestExtension.extensionContext = freContext;
        AndroidAttestExtension.appContext = freContext.getActivity().getApplicationContext();

        AndroidAttestExtension.dispatch(AndroidAttestActions.INIT, "{}");
    }

}
