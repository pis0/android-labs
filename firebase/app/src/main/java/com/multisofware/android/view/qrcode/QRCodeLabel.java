package com.multisofware.android.view.qrcode;

import android.app.Activity;
import android.content.Context;
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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, 100);
        super.setLayoutParams(params);

        super.setText(("Posicione o QRCode e toque na tela").toUpperCase());
//        super.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        super.setGravity(Gravity.CENTER_HORIZONTAL);
        super.setTextColor(0xffaaaaaa);
        super.setBackgroundColor(0xcc333333);
        super.setPivotX(0);
        super.setPivotY(0);
        super.setX(0);
        super.setY((height * 0.1f) - 50);
        super.setPadding(40, 20, 0, 0);
        super.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);


    }

}


