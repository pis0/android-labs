package com.multisofware.android.view.tickets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

public class TicketsLabel extends AppCompatTextView {


    public TicketsLabel(Context context) {
        super(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height, 100);
        super.setLayoutParams(params);

        super.setText(("Posicione o Canhoto na linha").toUpperCase());
        super.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        super.setTextColor(0xffaaaaaa);
        super.setBackgroundColor(0xcc333333);
        super.setPivotX(0);
        super.setPivotY(0);
        super.setRotation(90);
        super.setX((width / 3) * 2 + params.height);
        super.setPadding(40, 20, 0, 0);
        super.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);


    }

}


