package com.multisofware.android.view.qrcode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

public class QRCodeLabel extends AppCompatTextView {


    public QRCodeLabel(Context context) {
        super(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        String text = "Posicione o QRCode e toque na tela";

        super.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);

        Rect rect = new Rect();
        super.getTextMetricsParamsCompat().getTextPaint().getTextBounds(text, 0, text.length(), rect);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, rect.height() * 2);
        super.setLayoutParams(params);

        super.setText(text.toUpperCase());
        super.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        super.setTextColor(0xffaaaaaa);
        super.setBackgroundColor(0xcc333333);
        super.setPivotX(0);
        super.setPivotY(0);
        super.setX(0);
        super.setY((height * 0.1f) - rect.height());


    }

}


