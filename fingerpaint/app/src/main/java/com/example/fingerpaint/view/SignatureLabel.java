package com.example.fingerpaint.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

public class SignatureLabel extends AppCompatTextView {

    private ShapeDrawable line;

    public SignatureLabel(Context context) {
        super(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        String text = "Signature";

        super.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);

        Rect rect = new Rect();
        super.getTextMetricsParamsCompat().getTextPaint().getTextBounds(text, 0, text.length(), rect);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height, rect.height() * 2);
        super.setLayoutParams(params);

        super.setText(text.toUpperCase());
        super.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        super.setTextColor(0xffaaaaaa);
        super.setPivotX(0);
        super.setPivotY(0);
        super.setRotation(90);
        super.setX((int) (width * 0.2) - 2 );
        super.setPadding((int) (height *0.075f), 0, 0, 0);
    }

}
