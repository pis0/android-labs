package com.assukar.air.android.attest {
import com.assukar.air.android.attest.actions.AndroidAttestActions;

import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.StatusEvent;
import flash.external.ExtensionContext;

public class AndroidAttestController extends EventDispatcher {

    private static var _instance:AndroidAttestController;
    private var extContext:ExtensionContext;

    function AndroidAttestController() {
        super();

        extContext = ExtensionContext.createExtensionContext("com.assukar.air.android.attest", "");
        if (!extContext) throw new Error("this native extension is not supported on this platform.");
    }

    private function onStatus(e:StatusEvent):void {
        var eventToDisptch:Event = null;
        if (e.code.indexOf("AndroidAttestActions") != -1) eventToDisptch = new AndroidAttestActions(e.code, e.level, false, false);
        if (eventToDisptch) dispatchEvent(eventToDisptch);
    }

    public static function get instance():AndroidAttestController {
        if (!_instance) _instance = new AndroidAttestController();
        return _instance;
    }

    public function init():void {
        extContext.addEventListener(StatusEvent.STATUS, onStatus, false, 0, true);
        extContext.call("init");
    }

    public function attest():void {
        extContext.call("attest");
    }


}
}
