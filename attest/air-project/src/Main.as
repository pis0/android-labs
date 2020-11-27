package
{
    
    import com.assukar.air.android.utils.AndroidUtilsController;
    import com.assukar.air.android.utils.events.AndroidUtilsEvent;
    
    import flash.display.Sprite;
    import flash.display.StageAlign;
    import flash.text.TextField;
    import flash.text.TextFieldAutoSize;
    import flash.text.TextFormat;
    
    public class Main extends Sprite
    {
        
        private var androidLibController:AndroidUtilsController;
        private var textField:TextField;
        
        function Main()
        {
            
            stage.align = StageAlign.TOP_LEFT;
            
            
            textField = new TextField();
            textField.autoSize = TextFieldAutoSize.LEFT;
            textField.defaultTextFormat = new TextFormat(null, 24);
            
            textField.text = "Hello, World";
            
            addChild(textField);
            
            androidLibController = AndroidUtilsController.instance;
            
            if (androidLibController)
            {
                androidLibController.addEventListener(AndroidUtilsEvent.INIT, onInit);
                androidLibController.addEventListener(AndroidUtilsEvent.SUCCESS, onSuccess);
                androidLibController.addEventListener(AndroidUtilsEvent.ERROR, onError);
                
                androidLibController.init();
                
            }
            
            
        }
        
        private function onInit(e:AndroidUtilsEvent):void
        {
            trace("onInit - data:" + e.data);
            
            androidLibController.getSharedPreferencesUID();
        }
        
        private function onSuccess(e:AndroidUtilsEvent):void
        {
            trace("onSuccess - data:" + e.data);
            
            var result:Object = JSON.parse(e.data);
            if (result.hasOwnProperty("sharedPreferencesUID")) textField.text = result["sharedPreferencesUID"];
            
        }
        
        private function onError(e:AndroidUtilsEvent):void
        {
            trace("onError - data:" + e.data);
        }
        
        
    }
}
