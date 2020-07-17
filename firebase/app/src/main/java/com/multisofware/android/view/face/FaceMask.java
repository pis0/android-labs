package com.multisofware.android.view.face;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class FaceMask extends View {

    private Paint mTransparentPaint;
    private Path mPath = new Path();
    private RectF rect0;
    private RectF rect1;

    public FaceMask(Context context) {
        super(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        mTransparentPaint = new Paint();
        mTransparentPaint.setColor(Color.TRANSPARENT);
        mTransparentPaint.setStrokeWidth(10);

        float wAux = (width * 0.85f);
        float hAux = (height * 0.15f);

        rect0 = new RectF(
                (width - wAux) / 2,
                hAux,
                ((width - wAux) / 2) + wAux,
                hAux + height * 0.5f
        );

        rect1 = new RectF(
                (width - wAux) / 2,
                hAux,
                ((width - wAux) / 2) + wAux,
                hAux + height * 0.7f
        );

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPath.reset();

        mPath.addRoundRect(rect0, 90 * 6, 90 * 6, Path.Direction.CW);
        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        canvas.drawRoundRect(rect0, 90 * 6, 90 * 6, mTransparentPaint);
        canvas.drawPath(mPath, mTransparentPaint);
        canvas.clipPath(mPath);

        mPath.addOval(rect1, Path.Direction.CW);
        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        canvas.drawOval(rect1, mTransparentPaint);
        canvas.drawPath(mPath, mTransparentPaint);
        canvas.clipPath(mPath);


        canvas.drawColor(0x99000000);

    }

}


