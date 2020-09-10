package com.example.fingerpaint.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;

public class SignatureBtn extends AppCompatButton {


    DisplayMetrics displayMetrics;

    public SignatureBtn(final Context context, final String label, final View.OnClickListener listener) {
        super(context);

        displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        super.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        super.setText(label);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        super.setLayoutParams(params);

        super.setTextColor(0xff777777);
        super.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e1e1e1")));

        super.setOnClickListener(listener);

    }

}
