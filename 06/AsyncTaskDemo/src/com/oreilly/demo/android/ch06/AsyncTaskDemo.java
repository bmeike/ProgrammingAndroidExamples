package com.oreilly.demo.android.ch06;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.oreilly.demo.android.ch06.R;
import com.oreilly.demo.android.ch06.game.Game;


/** AsyncTaskDemo */
public class AsyncTaskDemo extends Activity {

    private final class AsyncInitGame
        extends AsyncTask<String, Void, String>
    {
        private final View root;
        private final Game game;
        private final TextView message;
        private final Drawable bg;

        public AsyncInitGame(
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
            root.setBackgroundColor(Color.CYAN);
            return ((1 != args.length) || (null == args[0]))
                ? null
                : game.initialize(args[0]);
        }
    }

    int mInFlight;

    /** @see android.app.Activity#onCreate(android.os.Bundle) */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.asyncdemo);

        final View root = findViewById(R.id.root);
        final Drawable bg = root.getBackground();

        final TextView msg = ((TextView) findViewById(R.id.msg));

        final Game game = Game.newGame();
        
        ((Button) findViewById(R.id.start)).setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View v) {
                    //initGame(
                    new AsyncInitGame(
                        root,
                        bg,
                        game,
                        //msg,
                        //"basic");
                        msg)
                    .execute("basic");
                } });
    }

    /**
     * Synchronous request to remote service
     * DO NOT USE!!
     */
    void initGame(
        View root,
        Drawable bg,
        Game game,
        TextView resp,
        String level)
    {
        // if the animation hasn't been started,
        // initialize and start it
        if (0 >= mInFlight++ ) {
            root.setBackgroundResource(R.anim.dots);
            ((AnimationDrawable) root.getBackground()).start();
        }

        // get the response from the remote service
        String msg = game.initialize(level);

        // if this is the last running initialization
        // remove and clean up the animation
        if (0 >= --mInFlight) {
            ((AnimationDrawable) root.getBackground()).stop();
            root.setBackgroundDrawable(bg);
        }

        resp.setText(msg);
    }
}