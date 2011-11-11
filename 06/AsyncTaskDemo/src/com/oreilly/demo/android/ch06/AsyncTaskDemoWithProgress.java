package com.oreilly.demo.android.ch06;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oreilly.demo.android.ch06.R;
import com.oreilly.demo.android.ch06.game.Game;


/** AsyncTaskDemo */
public class AsyncTaskDemoWithProgress extends Activity {

    private final class AsyncInit
        extends AsyncTask<String, Integer, String>
        implements Game.InitProgressListener
    {
        private final View root;
        private final Game game;
        private final TextView message;
        private final Drawable bg;

        public AsyncInit(
            View root,
            Drawable bg,
            Game game,
            TextView msg)
        {
            this.root = root;
            this.bg = bg;
            this.game = game;
            this.message = msg;
        }

        // runs on the UI thread
        @Override protected void onPreExecute() {
            if (0 >= mInFlight++) {
                root.setBackgroundResource(R.anim.dots);
                ((AnimationDrawable) root.getBackground()).start();
            }
        }

        // runs on the UI thread
        @Override protected void onPostExecute(String msg) {
            if (0 >= --mInFlight) {
                ((AnimationDrawable) root.getBackground()).stop();
                root.setBackgroundDrawable(bg);
            }

            message.setText(msg);
        }

        // runs on its own thread
        @Override protected String doInBackground(String... args) {
            return ((1 != args.length) || (null == args[0]))
                ? null
                : game.initialize(args[0], this);
        }

        // runs on the UI thread
        @Override protected void onProgressUpdate(Integer... vals) {
            updateProgressBar(vals[0].intValue());
        }

        // runs on its own thread
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
