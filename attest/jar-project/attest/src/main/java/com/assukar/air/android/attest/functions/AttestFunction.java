package com.assukar.air.android.attest.functions;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.assukar.air.android.attest.AndroidAttestExtension;
import com.assukar.air.android.attest.actions.AndroidAttestActions;
//import com.assukar.android.attest.statics.AndroidAttestStatics;

public class AttestFunction implements FREFunction {

    private static final String TAG = "AndroidUtils";

    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        Log.v(TAG, "call");
        attest();

        return null;
    }

    private void attest() {
        //AndroidAttestExtension.dispatch(AndroidAttestActions.ATTEST, AndroidAttestStatics. (AndroidAttestExtension.appContext));
    }

}
