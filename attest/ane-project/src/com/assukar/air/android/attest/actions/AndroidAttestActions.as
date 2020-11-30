package com.assukar.air.android.attest.actions {
import flash.events.Event;

public class AndroidAttestActions extends Event {


    public static const INIT:String = "INIT_AndroidAttestActions";
    public static const ATTEST:String = "ATTEST_AndroidAttestActions";

    public var data:String;

    function AndroidAttestActions(type:String, data:String, bubbles:Boolean = false, cancelable:Boolean = false) {
        this.data = data;
        super(type, bubbles, cancelable);
    }
}
}
