package com.oreilly.demo.android.pa.asynctask;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oreilly.demo.android.pa.asynctask.R;
import com.oreilly.demo.android.pa.asynctask.game.Game;


/** AsyncTaskDemo */
public class AsyncTaskDemoWithProgress extends Activity {

    private final class AsyncInit
        extends AsyncTask<String, Integer, String>
        implements Game.InitProgressListener
    {
        private final View dots;
        private final Game game;
        private final TextView message;
        private final Drawable bg;

        public AsyncInit(
            View dots,
            Drawable bg,
            Game game,
            TextView msg)
        {
            this.dots = dots;
            this.bg = bg;
            this.game = game;
            this.message = msg;
        }

        // runs on the UI thread
        @Override protected void onPreExecute() {
            if (0 >= mInFlight++) {
                dots.setBackgroundResource(R.anim.dots);
                ((AnimationDrawable) dots.getBackground()).start();
            }
        }

        // runs on the UI thread
        @Override protected void onPostExecute(String msg) {
            if (0 >= --mInFlight) {
                ((AnimationDrawable) dots.getBackground()).stop();
                dots.setBackgroundDrawable(bg);
            }

            message.setText(msg);
        }

        // runs on its own thread
        @Override protected String doInBackground(String... args) {
            return ((1 != args.length) || (null == args[0]))
                ? null
                : game.initialize(args[0], this);
        }

        @Override protected void onProgressUpdate(Integer... vals) {
            updateProgressBar(vals[0].intValue());
        }

        @Override public void onInitProgress(int pctComplete) {
            publishProgress(Integer.valueOf(pctComplete));
        }
    }

    int mInFlight;
    int mComplete;

    /** @see android.app.Activity#onCreate(android.os.Bundle) */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.asyncdemoprogress);

        final View dots = findViewById(R.id.dots);
        final Drawable bg = dots.getBackground();

        final TextView msg = ((TextView) findViewById(R.id.msg));

        final Game game = Game.newGame();

        ((Button) findViewById(R.id.start)).setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View v) {
                    mComplete = 0;
                    new AsyncInit(
                        dots,
                        bg,
                        game,
                        msg)
                    .execute("basic");
                } });
    }

    void updateProgressBar(int progress) {
        int p = progress;
        if (mComplete < p) {
            mComplete = p;
            ((ProgressBar) findViewById(R.id.progress))
                .setProgress(p);
        }
    }
}
