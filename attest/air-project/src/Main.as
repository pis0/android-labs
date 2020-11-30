package {

import com.assukar.air.android.attest.AndroidAttestController;
import com.assukar.air.android.attest.actions.AndroidAttestActions;

import flash.display.Sprite;
import flash.display.StageAlign;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class Main extends Sprite {

    private var androidLibController:AndroidAttestController;
    private var textField:TextField;

    function Main() {

        stage.align = StageAlign.TOP_LEFT;


        textField = new TextField();
        textField.autoSize = TextFieldAutoSize.LEFT;
        textField.defaultTextFormat = new TextFormat(null, 24);

        textField.text = "Hello, World";

        addChild(textField);

        androidLibController = AndroidAttestController.instance;

        if (androidLibController) {
            androidLibController.addEventListener(AndroidAttestActions.INIT, onInit);
            androidLibController.addEventListener(AndroidAttestActions.ATTEST, onAttest);

            androidLibController.init();

        }


    }

    private function onInit(e:AndroidAttestActions):void {
        trace("onInit - data:" + e.data);

        androidLibController.attest();
    }

    private function onAttest(e:AndroidAttestActions):void {
        trace("onAttest - data:" + e.data);
        textField.text = e.data;
    }


}
}
