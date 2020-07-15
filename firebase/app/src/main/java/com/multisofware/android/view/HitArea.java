package com.multisofware.android.view;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

public class HitArea extends AppCompatTextView {


    public HitArea(Context context) {
        super(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        super.setLayoutParams(params);

        super.setBackgroundColor(0x55de0000);


    }

}


