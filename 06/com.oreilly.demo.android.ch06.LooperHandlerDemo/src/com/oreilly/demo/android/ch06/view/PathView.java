package com.oreilly.demo.android.ch06.view;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import android.view.View;

import com.oreilly.demo.android.ch06.model.Path;
import com.oreilly.demo.android.ch06.model.Point;


/**
 * I see spots!
 *
 * @author <a href="mailto:android@callmeike.net">Blake Meike</a>
 */
public class PathView extends View {
    private final Path mPath;

    /**
     * @param context the rest of the application
     * @param path the path we draw
     */
    public PathView(Context context, Path path) {
        super(context);
        this.mPath = path;
        setMinimumWidth(180);
        setMinimumHeight(200);
        setFocusable(true);
    }
        
    /**
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
            getSuggestedMinimumWidth(),
            getSuggestedMinimumHeight());
    }

    /**
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        
        Paint paint = new Paint();
        paint.setStyle(Style.STROKE);
        paint.setColor(hasFocus() ? Color.BLUE : Color.GRAY);
        canvas.drawRect(0, 0, getWidth() - 1, getHeight() -1, paint);
 
        paint.setStyle(Style.FILL);
        for (Point point : mPath.getPoints()) {
            paint.setColor(point.getColor());
            canvas.drawCircle(
                point.getX(),
                point.getY(),
                point.getDiameter(),
                paint);
        }
    }
}
