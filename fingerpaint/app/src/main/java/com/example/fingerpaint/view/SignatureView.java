package com.example.fingerpaint.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.DisplayMetrics;
import android.view.View;

public class SignatureView extends View {

    private ShapeDrawable line;

    public SignatureView(Context context) {
        super(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        line = new ShapeDrawable(new RectShape());
        line.getPaint().setColor(0xcc999999);
        line.setBounds(
                (int) (width * 0.2),
                (int) (height * 0.05),
                (int) ((width * 0.2) + 2),
                (int) (height * 0.9)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        line.draw(canvas);
    }
}
