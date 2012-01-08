package com.oreilly.demo.android.pa.viewdemo.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;


/** HelloAndroidTextDrawable */
public class HelloAndroidTextDrawable extends Drawable {
    private ColorFilter filter;
    private int opacity;

    /**
     * @see android.graphics.drawable.Drawable#draw(
     *    android.graphics.Canvas)
     */
    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();

        paint.setColorFilter(filter);
        paint.setAlpha(opacity);

        paint.setTextSize(12);
        paint.setColor(Color.GREEN);
        canvas.drawText("Hello", 40, 55, paint);

        paint.setTextSize(16);
        paint.setColor(Color.RED);
        canvas.drawText("Android", 35, 65, paint);
    }

    /** @see android.graphics.drawable.Drawable#getOpacity() */
    @Override
    public int getOpacity() { return PixelFormat.TRANSLUCENT; }

    /** @see android.graphics.drawable.Drawable#setAlpha(int) */
    @Override
    public void setAlpha(int alpha) { opacity = alpha; }

    /**
     * @see android.graphics.drawable.Drawable#setColorFilter(
     *     android.graphics.ColorFilter)
     */
    @Override
    public void setColorFilter(ColorFilter cf) { filter = cf; }
}