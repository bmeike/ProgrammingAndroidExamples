/* $Id: $
 */
package com.oreilly.demo.android.pa.contactviewer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


/**
 * ContactDetailActivity
 */
public class ContactDetailActivity extends FragmentActivity {
    private ContactDetails details;


    /** @see android.support.v4.app.Fragment#onCreate(android.os.Bundle) */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.contact_detail);

        details = new ContactDetails(
            (null != state) ? state : getIntent().getExtras());

        details.setView(findViewById(R.id.contacts_detail_content));

        details.populateContact();

        details.createLoaders(this);
        details.initLoaders(getSupportLoaderManager());
    }

    /** @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle) */
    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (null != details) { details.saveToBundle(state); }
    }
}
