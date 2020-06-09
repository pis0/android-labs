package com.multisofware.android.view.tickets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.DisplayMetrics;
import android.view.View;

public class TicketsMask extends View {

    private ShapeDrawable topRect;
    private ShapeDrawable bottomRect;
    private ShapeDrawable line;

    public TicketsMask(Context context) {
        super(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        bottomRect = new ShapeDrawable(new RectShape());
        bottomRect.getPaint().setColor(0x99000000);
        bottomRect.setBounds(0, 0, width / 3, height);

        topRect = new ShapeDrawable(new RectShape());
        topRect.getPaint().setColor(0x99000000);
        topRect.setBounds(width - (width / 3), 0, (width - (width / 3)) + (width / 3), height);

        line = new ShapeDrawable(new RectShape());
        line.getPaint().setColor(0xccde0000);
        line.setBounds((width / 2) - 2, 0, ((width / 2) - 2) + 2, height);


    }


    protected void onDraw(Canvas canvas) {
        bottomRect.draw(canvas);
        topRect.draw(canvas);
        line.draw(canvas);
    }

}


