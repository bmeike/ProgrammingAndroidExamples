/* $Id: $
 */
package com.oreilly.demo.android.pa.simplefragment;

import java.util.Date;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * ContactViewer
 */
public class SimpleFragment extends Activity {
    private static final String FRAG1_TAG
        = SimpleFragment.class.getCanonicalName() + ".fragment1";
    private static final String FRAG2_TAG
        = SimpleFragment.class.getCanonicalName() + ".fragment2";

    /** @see android.app.Activity#onCreate(android.os.Bundle) */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.main);

        ((Button) findViewById(R.id.new_fragments))
            .setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) { update(); }
                });

        Date time = new Date();
        FragmentManager fragMgr = getFragmentManager();
        FragmentTransaction xact = fragMgr.beginTransaction();
        if (null == fragMgr.findFragmentByTag(FRAG1_TAG)) {
            xact.add(
                R.id.date_time,
                DateTime.newInstance(time),
                FRAG1_TAG);
        }
        if (null == fragMgr.findFragmentByTag(FRAG2_TAG)) {
            xact.add(
                R.id.date_time2,
                DateTime.newInstance(time),
                FRAG2_TAG);
        }
        xact.commit();
    }

    void update() {
        Date time = new Date();
        FragmentTransaction xact
            = getFragmentManager().beginTransaction();

        xact.replace(
            R.id.date_time,
            DateTime.newInstance(time),
            FRAG1_TAG);

        xact.replace(
            R.id.date_time2,
            DateTime.newInstance(time),
            FRAG2_TAG);

        xact.addToBackStack(null);
        xact.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        xact.commit();
    }
}
