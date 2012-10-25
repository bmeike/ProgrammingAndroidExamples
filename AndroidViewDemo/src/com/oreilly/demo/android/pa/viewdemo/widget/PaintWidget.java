package com.oreilly.demo.android.pa.viewdemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PaintWidget extends View {

	public PaintWidget(Context context, AttributeSet attrs, int defStyle) {
		this(context);
	}

	public PaintWidget(Context context, AttributeSet attrs) {
		this(context);
	}

	public PaintWidget(Context context) { super(context); }

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(100, 100);
	}

	@Override protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);

		Paint paint = new Paint();
		canvas.drawLine(33, 0, 33, 100, paint);

		paint.setColor(Color.RED);
		paint.setStrokeWidth(10);
		canvas.drawLine(56, 0, 56, 100, paint);

		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(5);

		for (int y = 30, alpha = 255; alpha > 2; alpha >>= 1, y += 10) {
			paint.setAlpha(alpha);
			canvas.drawLine(0, y, 100, y, paint);
		}
	}
}
