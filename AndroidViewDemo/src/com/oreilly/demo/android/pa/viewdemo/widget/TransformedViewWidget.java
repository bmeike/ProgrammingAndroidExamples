/* $Id: $ */
package com.oreilly.demo.android.pa.viewdemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;


/**
 *
 */
public class TransformedViewWidget extends View {

    /**
     * Transformation
     */
    public interface Transformation {
        /** @param canvas */
        void transform(Canvas canvas);
        /** @return text descriptiont of the transform. */
        String describe();
    }

    private final Transformation transformation;
    private final Drawable drawable;

    /**
     * @param context
     * @param draw
     * @param xform
     */
    public TransformedViewWidget(
        Context context,
        Drawable draw,
        Transformation xform)
    {
        super(context);

        drawable = draw;
        transformation = xform;

        setMinimumWidth(160);
        setMinimumHeight(135);
    }

    /**
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
            ViewGroup.LayoutParams.MATCH_PARENT,
            getSuggestedMinimumHeight());
    }

    /**
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        canvas.save();
        transformation.transform(canvas);
        drawable.draw(canvas);
        canvas.restore();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        Rect r = canvas.getClipBounds();
        canvas.drawRect(r, paint);

        paint.setTextSize(10);
        paint.setColor(Color.BLUE);
        canvas.drawText(
            transformation.describe(),
            5,
            getMeasuredHeight() - 5,
            paint);
    }
}