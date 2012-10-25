package com.oreilly.demo.android.pa.uidemo;

import java.util.ArrayList;
import java.util.List;
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

import com.oreilly.demo.android.pa.uidemo.model.Dot;
import com.oreilly.demo.android.pa.uidemo.model.Dots;
import com.oreilly.demo.android.pa.uidemo.view.DotView;


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
        private List<Integer> tracks = new ArrayList<Integer>();

        TrackingTouchListener(Dots dots) { mDots = dots; }

        @Override public boolean onTouch(View v, MotionEvent evt) {
            int n;
            int idx;
            int action = evt.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    idx = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    tracks.add(Integer.valueOf(evt.getPointerId(idx)));
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    idx = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    tracks.remove(Integer.valueOf(evt.getPointerId(idx)));
                    break;

                case MotionEvent.ACTION_MOVE:
                    n = evt.getHistorySize();
                    for (Integer i: tracks) {
                        idx = evt.findPointerIndex(i.intValue());
                        for (int j = 0; j < n; j++) {
                            addDot(
                                mDots,
                                evt.getHistoricalX(idx, j),
                                evt.getHistoricalY(idx, j),
                                evt.getHistoricalPressure(idx, j),
                                evt.getHistoricalSize(idx, j));
                        }
                    }
                    break;


                default:
                    return false;
            }

            for (Integer i: tracks) {
                idx = evt.findPointerIndex(i.intValue());
                addDot(
                    mDots,
                    evt.getX(idx),
                    evt.getY(idx),
                    evt.getPressure(idx),
                    evt.getSize(idx));
            }

            return true;
        }

        private void addDot(Dots dots, float x, float y, float p, float s) {
            dots.addDot(
                x,
                y,
                Color.CYAN,
                (int) ((p + 0.5) * (s + 0.5) * DOT_DIAMETER));
        }
    }

    /** Generate new dots, one per second. */
    private final class DotGenerator implements Runnable {
        final Dots dots;
        final DotView view;
        final int color;

        private final Handler hdlr = new Handler();
        private final Runnable makeDots = new Runnable() {
            @Override public void run() { makeDot(dots, view, color); }
        };

        private volatile boolean done;

        DotGenerator(Dots dots, DotView view, int color) {
            this.dots = dots;
            this.view = view;
            this.color = color;
        }

        public void done() { done = true; }

        @Override
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

        // install the view
        setContentView(R.layout.main);

        // find the dots view
        dotView = (DotView) findViewById(R.id.dots);
        dotView.setDots(dotModel);

        dotView.setOnCreateContextMenuListener(this);
        dotView.setOnTouchListener(new TrackingTouchListener(dotModel));

        dotView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.ACTION_DOWN != event.getAction()) {
                    return false;
                }

                int color;
                switch (keyCode) {
                    case KeyEvent.KEYCODE_SPACE:
                        color = Color.MAGENTA;
                        break;
                    case KeyEvent.KEYCODE_ENTER:
                        color = Color.BLUE;
                        break;
                    default:
                        return false;
                }

                makeDot(dotModel, dotView, color);

                return true;
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
                // This code makes the UI unacceptably unresponsive.
                // ... investigating
                //tb1.setText((null == d) ? "" : String.valueOf(d.getX()));
                //tb2.setText((null == d) ? "" : String.valueOf(d.getY()));
                dotView.invalidate();
            } });
    }

    /** Install an options menu. */
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.simple_menu, menu);
        return true;
    }

    /** Respond to an options menu selection. */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                dotModel.clearDots();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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