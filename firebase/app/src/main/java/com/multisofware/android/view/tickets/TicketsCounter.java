package com.multisofware.android.view.tickets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

public class TicketsCounter extends AppCompatTextView {


    public TicketsCounter(Context context) {
        super(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        String text = "Validados: ";

        super.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);

        Rect rect = new Rect();
        super.getTextMetricsParamsCompat().getTextPaint().getTextBounds(text, 0, text.length(), rect);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height, rect.height() * 2);
        super.setLayoutParams(params);

        super.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        super.setTextColor(0xffaaaaaa);
        super.setBackgroundColor(0xcc333333);
        super.setPivotX(0);
        super.setPivotY(0);
        super.setRotation(90);
        super.setX(width / 3);
        super.setPadding((int) (height *0.05f), 0, 0, 0);

        setCounter(0, 0);
    }

    public void setCounter(int validated, int total) {
        super.setText(("Validados: " + validated + "/" + total).toUpperCase());
    }

}


