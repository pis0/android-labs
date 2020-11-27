package com.assukar.air.android.utils
{
    import com.assukar.air.android.utils.events.AndroidUtilsEvent;
    
    import flash.events.Event;
    import flash.events.EventDispatcher;
    import flash.events.StatusEvent;
    import flash.external.ExtensionContext;
    
    public class AndroidUtilsController extends EventDispatcher
    {
        
        private static var _instance:AndroidUtilsController;
        private var extContext:ExtensionContext;
        
        function AndroidUtilsController()
        {
            super();
            
            extContext = ExtensionContext.createExtensionContext("com.assukar.air.android.utils", "");
            if (!extContext) throw new Error("this native extension is not supported on this platform.");
        }
        
        private function onStatus(e:StatusEvent):void
        {
            var eventToDisptch:Event = null;
            if (e.code.indexOf("AndroidUtilsEvent") != -1) eventToDisptch = new AndroidUtilsEvent(e.code, e.level, false, false);
            if (eventToDisptch) dispatchEvent(eventToDisptch);
        }
        
        public static function get instance():AndroidUtilsController
        {
            if (!_instance) _instance = new AndroidUtilsController();
            return _instance;
        }
        
        public function init():void
        {
            extContext.addEventListener(StatusEvent.STATUS, onStatus, false, 0, true);
            extContext.call("init");
        }
        
        public function getSharedPreferencesUID():void
        {
            extContext.call("getSharedPreferencesUID");
        }
        
        
    }
}
