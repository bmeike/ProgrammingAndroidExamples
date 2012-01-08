/* $Id: $
 */
package com.oreilly.demo.android.pa.contactviewer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.TextView;


/**
 * ContactDetails
 */
public class ContactDetails {
    /** Bundle tag for Id */
    public static final String TAG_ID = "contactId";

    /** Bundle tag for Contact */
    public static final String TAG_CONTACT = "contact";

    /** Loader Id for Email loader */
    public static final int LOADER_EMAIL = 0;

    /** Loader Id for Phone loader */
    public static final int LOADER_PHONE = 1;

    private final String contactId;
    private final String contact;

    private final Map<Integer, ContactDetailsLoader> loaders
        = new HashMap<Integer, ContactDetailsLoader>();

    private View view;

    /**
     * @param state null or parameters
     */
    public ContactDetails(Bundle state) {
        this((null == state) ? null : state.getString(TAG_ID),
             (null == state) ? null : state.getString(TAG_CONTACT));
    }

    /**
     * @param contactId the contact for details
     * @param contact contact's display name
     */
    public ContactDetails(String contactId, String contact) {
        this.contactId = contactId;
        this.contact = contact;
    }

    /** @param view the view */
    public void setView(View view) { this.view = view; }

    /** @param state saved state */
    public void saveToBundle(Bundle state) {
        state.putString(TAG_ID, contactId);
        state.putString(TAG_CONTACT, contact);
    }

    /** Populate the contact name */
    public void populateContact() {
        if ((null == view) || (null == contact)) { return; }

        ((TextView) view.findViewById(R.id.contact_detail_name))
            .setText(contact);
    }

    /**
     * Populate text views from the cursor.
     *
     * @param cursor the cursor with the data
     * @param from the data columns containing details
     * @param to the view text edit field for displaying data
     */
    public void populateFields(
        Cursor cursor,
        String[] from,
        int[] to)
    {
        if (null == view) { return; }

        for (int i = 0; i < from.length; i++ ) {
            ((TextView) view.findViewById(to[i])).setText(cursor
                .getString(cursor.getColumnIndex(from[i])));
        }
    }

    /** @param act the Activity context */
    public void createLoaders(Activity act) {
        if (null == contactId) { return; }

        loaders.put(
            Integer.valueOf(LOADER_PHONE),
            new ContactDetailsLoader(
                this,
                act,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                contactId,
                new String[] {
                    ContactsContract.CommonDataKinds.Phone.NUMBER },
                new int[] { R.id.contact_detail_phone }));

        loaders.put(
            Integer.valueOf(LOADER_EMAIL),
            new ContactDetailsLoader(
                this,
                act,
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                contactId,
                new String[] {
                    ContactsContract.CommonDataKinds.Email.DATA },
                new int[] { R.id.contact_detail_email }));
    }

    /**
     * @param loadMgr the loadMgr with which to init the loaders
     * @return unmodifiable map of tags to loaders
     */
    public Map<Integer, ContactDetailsLoader> initLoaders(
        LoaderManager loadMgr)
    {
        for (Map.Entry<Integer, ContactDetailsLoader> loader:
            loaders.entrySet())
        {
            loadMgr.initLoader(
                loader.getKey().intValue(),
                null,
                loader.getValue());
        }

        return Collections.unmodifiableMap(loaders);
    }
}
