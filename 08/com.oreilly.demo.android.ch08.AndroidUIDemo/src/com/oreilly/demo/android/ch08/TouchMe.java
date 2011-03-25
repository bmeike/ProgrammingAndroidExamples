package com.oreilly.demo.android.ch08;

import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.oreilly.demo.android.ch08.R;
import com.oreilly.demo.android.ch08.model.Dot;
import com.oreilly.demo.android.ch08.model.Dots;
import com.oreilly.demo.android.ch08.view.DotView;


/**
 * Android UI demo program
 */
public class TouchMe extends Activity {
    /** Dot diameter */
    public static final int DOT_DIAMETER = 6;

    /** Listen for taps. */
    private static final class TrackingTouchListener
        implements View.OnTouchListener
    {
        private final Dots mDots;

        TrackingTouchListener(Dots dots) { mDots = dots; }

        @Override public boolean onTouch(View v, MotionEvent evt) {
            switch (evt.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_MOVE:
                    for (int i = 0, n = evt.getHistorySize(); i < n; i++) {
                        addDot(
                            mDots,
                            evt.getHistoricalX(i),
                            evt.getHistoricalY(i),
                            evt.getHistoricalPressure(i),
                            evt.getHistoricalSize(i));
                    }
                    break;

                default:
                    return false;
            }

            addDot(
                mDots,
                evt.getX(),
                evt.getY(),
                evt.getPressure(),
                evt.getSize());

            return true;
        }

        private void addDot(Dots dots, float x, float y, float p, float s) {
            dots.addDot(
                x,
                y,
                Color.CYAN,
                (int) ((p * s * DOT_DIAMETER) + 1));
        }
    }

    /** Generate new dots, one per second. */
    private final class DotGenerator implements Runnable {
        final Dots dots;
        final DotView view;
        final int color;

        private final Handler hdlr = new Handler();
        private final Runnable makeDots = new Runnable() {
            public void run() { makeDot(dots, view, color); }
        };

        private volatile boolean done;

        DotGenerator(Dots dots, DotView view, int color) {
            this.dots = dots;
            this.view = view;
            this.color = color;
        }

        public void done() { done = true; }

        public void run() {
            while (!done) {
                hdlr.post(makeDots);
                try { Thread.sleep(5000); }
                catch (InterruptedException e) { }
            }
        }
    }

    private final Random rand = new Random();

    /** The application model */
    final Dots dotModel = new Dots();
    
    /** The application view */
    DotView dotView;

    /** The dot generator */
    DotGenerator dotGenerator;

    /** Called when the activity is first created. */
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);

        dotView = new DotView(this, dotModel);
        dotView.setOnCreateContextMenuListener(this);
        
        // install the view
        setContentView(R.layout.main);
        ((LinearLayout) findViewById(R.id.root)).addView(dotView, 0);

        dotView.setOnTouchListener(new TrackingTouchListener(dotModel));

        dotView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.ACTION_UP != event.getAction()) {
                    int color = Color.BLUE;
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_SPACE:
                            color = Color.MAGENTA;
                            break;
                        case KeyEvent.KEYCODE_ENTER:
                            color = Color.YELLOW;
                            break;
                        default: ;
                    }

                    makeDot(dotModel, dotView, color);
                }

                return (keyCode < KeyEvent.KEYCODE_0)
                    ||(keyCode > KeyEvent.KEYCODE_9);
            } });


        dotView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && (null != dotGenerator)) {
                    dotGenerator.done();
                    dotGenerator = null;
                }
                else if (hasFocus && (null == dotGenerator)) {
                    dotGenerator
                    = new DotGenerator(dotModel, dotView, Color.BLACK);
                    new Thread(dotGenerator).start();
                }
            } });

        // wire up the controller
        ((Button) findViewById(R.id.button1)).setOnClickListener(
            new Button.OnClickListener() {
                @Override public void onClick(View v) {
                    makeDot(dotModel, dotView, Color.RED);
                } });
        ((Button) findViewById(R.id.button2)).setOnClickListener(
            new Button.OnClickListener() {
                @Override public void onClick(View v) {
                    makeDot(dotModel, dotView, Color.GREEN);
                } });

        final EditText tb1 = (EditText) findViewById(R.id.text1);
        final EditText tb2 = (EditText) findViewById(R.id.text2);
        dotModel.setDotsChangeListener(new Dots.DotsChangeListener() {
            @Override public void onDotsChange(Dots dots) {
                Dot d = dots.getLastDot();
                tb1.setText((null == d) ? "" : String.valueOf(d.getX()));
                tb2.setText((null == d) ? "" : String.valueOf(d.getY()));
                dotView.invalidate();
            } });
    }
    
    /** Install an options menu. */
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, 1, Menu.NONE, "Clear")
            .setAlphabeticShortcut('x');
        return true;
    }
    
    /** Respond to an options menu selection. */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                dotModel.clearDots();
                return true;
            default: ;
        }

        return false;
    }

    /** Install a context menu. */
    @Override public void onCreateContextMenu(
        ContextMenu menu,
        View v,
        ContextMenuInfo menuInfo)
    {
        menu.add(Menu.NONE, 1, Menu.NONE, "Clear")
            .setAlphabeticShortcut('x');
    }
    
    /** Respond to a context menu selection. */
    @Override public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                dotModel.clearDots();
                return true;
            default: ;
        }

        return false;
    }

    /**
     * @param dots the dots we're drawing
     * @param view the view in which we're drawing dots
     * @param color the color of the dot
     */
    void makeDot(Dots dots, DotView view, int color) {
        int pad = (DOT_DIAMETER + 2) * 2;
        dots.addDot(
            DOT_DIAMETER + (rand.nextFloat() * (view.getWidth() - pad)),
            DOT_DIAMETER + (rand.nextFloat() * (view.getHeight() - pad)),
            color,
            DOT_DIAMETER);
    }
}