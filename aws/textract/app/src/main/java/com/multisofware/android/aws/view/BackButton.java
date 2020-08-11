package com.multisofware.android.aws.view;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageButton;

import com.multisofware.android.aws.R;

public class BackButton extends AppCompatImageButton {


    public BackButton(final Context context) {
        super(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
        super.setLayoutParams(params);

        super.setImageResource(R.mipmap.ic_back);
        super.setBackgroundColor(0x00);

        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finish();
            }
        });

    }

}


