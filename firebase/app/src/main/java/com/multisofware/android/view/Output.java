package com.multisofware.android.view;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

public class Output extends AppCompatTextView {


    public Output(Context context) {
        super(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, (int) (height * 0.3));
        super.setLayoutParams(params);

        super.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        super.setTextColor(0xffffffff);
        super.setBackgroundColor(0x55de0000);
        super.setPivotX(0);
        super.setPivotY(0);
//        super.setPadding(20, 20, 20, 20);
        super.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        super.setY(height - (int) (height * 0.3));


    }

}


