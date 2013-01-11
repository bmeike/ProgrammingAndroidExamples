/* $Id: $
 *
 */
package com.oreilly.demo.android.pa.viewdemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;

import com.oreilly.demo.android.pa.viewdemo.R;


/**
 * Simple widget, demonstrating the use of Paint
 */
public class EffectsWidget extends View {

    /** The effect to apply to the drawing */
    public interface PaintEffect {
        /** @param paint the paint effect */
        void setEffect(Paint paint);
    }

    private final PaintEffect effect;
    private final int id;

    /**
     * @param context
     * @param n
     * @param pe
     */
    public EffectsWidget(Context context, int n, PaintEffect pe) {
        super(context);

        id = n;

        effect = pe;

        setMinimumWidth(160);
        setMinimumHeight(135);

        setBackgroundResource(R.drawable.bg0);
    }

    /** @see android.view.View#onMeasure(int, int) */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
            ViewGroup.LayoutParams.MATCH_PARENT,
            getSuggestedMinimumHeight());
    }

    /** @see android.view.View#onDraw(android.graphics.Canvas) */
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        effect.setEffect(paint);
        paint.setColor(Color.DKGRAY);

        paint.setStrokeWidth(5);
        canvas.drawLine(20, 10, 140, 20, paint);

        paint.setTextSize(26);
        canvas.drawText("Android", 40, 50, paint);

        // create a new Paint to draw the widget structure
        paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawText(String.valueOf(id), 2.0F, 12.0F, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(canvas.getClipBounds(), paint);
    }
}
