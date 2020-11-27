package com.assukar.air.android.utils.functions;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.assukar.air.android.utils.AndroidUtilsExtension;
import com.assukar.air.android.utils.events.AndroidUtilsEvent;

public class InitFunction implements FREFunction {

    private static final String TAG = "AndroidUtils";

    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {


        Log.v(TAG, "call");
        initContext(freContext);

        return null;
    }


    private void initContext(FREContext freContext) {

        Log.v(TAG, "initContext");

        AndroidUtilsExtension.extensionContext = freContext;
        AndroidUtilsExtension.appContext = freContext.getActivity().getApplicationContext();

        AndroidUtilsExtension.dispatch(AndroidUtilsEvent.INIT, "{}");

    }

}
