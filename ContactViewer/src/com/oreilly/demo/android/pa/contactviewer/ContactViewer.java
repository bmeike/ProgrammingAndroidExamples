/* $Id: $
 */
package com.oreilly.demo.android.pa.contactviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


/**
 * ContactViewer
 */
public class ContactViewer extends FragmentActivity {
    private static final String FRAG_TAG
        = ContactViewer.class.getCanonicalName() + ".fragment";

    private boolean useFrag;

    /** @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle) */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.main);

        useFrag = null != findViewById(R.id.contact_detail);

        if (useFrag) { installDetailsFragment(); }
    }

    /**
     * @see android.support.v4.app.FragmentActivity#startActivityFromFragment(android.support.v4.app.Fragment, android.content.Intent, int)
     */
    @Override
    public void startActivityFromFragment(
        Fragment fragment,
        Intent intent,
        int requestCode)
    {
        if (!useFrag) { startActivity(intent); }
        else if (fragment instanceof ContactsFragment) {
            launchDetailFragment(intent.getExtras());
        }
    }

    private void installDetailsFragment() {
        FragmentManager fragMgr = getSupportFragmentManager();

        if (null != fragMgr.findFragmentByTag(FRAG_TAG)) { return; }

        FragmentTransaction xact = fragMgr.beginTransaction();
        xact.add(
            R.id.contact_detail,
            ContactDetailFragment.newInstance(),
            FRAG_TAG);
        xact.commit();
    }

    private void launchDetailFragment(Bundle xtra) {
        FragmentTransaction xact
            = getSupportFragmentManager().beginTransaction();

        xact.replace(
            R.id.contact_detail,
            ContactDetailFragment.newInstance(xtra),
            FRAG_TAG);

        xact.addToBackStack(null);
        xact.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        xact.commit();
    }
}
