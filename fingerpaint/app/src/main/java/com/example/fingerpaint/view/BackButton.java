package com.example.fingerpaint.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageButton;

import com.example.fingerpaint.R;

public class BackButton extends AppCompatImageButton {

    private static final String TAG = BackButton.class.getSimpleName();

    public BackButton(final Context context) {
        super(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_back, options);
        int iconWidth = options.outWidth;
        int iconHeight = options.outHeight;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(iconWidth, iconHeight);
        super.setLayoutParams(params);

        super.setImageResource(R.mipmap.ic_back);
        super.setColorFilter(0xff999999);
        super.setBackgroundColor(0x00);

        super.setRotation(90);
        super.setX(width - params.width);

        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "setOnClickListener");
                ((Activity) context).finish();
            }
        });

    }

}


