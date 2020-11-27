package com.assukar.air.android.utils.events
{
    import flash.events.Event;
    
    public class AndroidUtilsEvent extends Event
    {
        
        public static const INIT:String = "INIT_AndroidUtilsEvent";
        public static const SUCCESS:String = "SUCCESS_AndroidUtilsEvent";
        public static const ERROR:String = "ERROR_AndroidUtilsEvent";
        
        
        public var data:String;
        
        function AndroidUtilsEvent(type:String, data:String, bubbles:Boolean = false, cancelable:Boolean = false)
        {
            this.data = data;
            super(type, bubbles, cancelable);
        }
    }
}
