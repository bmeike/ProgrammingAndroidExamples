/* $Id: $
 */
package com.oreilly.demo.android.pa.contactviewer;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ImageView;


/**
 * ContactsCursorAdapter
 * Map the presence status to the correct icon
 */
public class ContactsCursorAdapter extends SimpleCursorAdapter {
    private static final String[] CONTACTS_FROM = new String[] {
        Contacts.CONTACT_PRESENCE,
        Contacts.DISPLAY_NAME };

    private static final int[] CONTACTS_TO
    = new int[] { R.id.presence, R.id.name };

    /**
     * Ctor: complete
     *
     * @param ctxt the context
     * @param cursor a cursor
     */
    public ContactsCursorAdapter(Context ctxt, Cursor cursor) {
        super(
            ctxt,
            R.layout.contacts_item,
            cursor,
            CONTACTS_FROM,
            CONTACTS_TO,
            0);
    }

    /** @see android.widget.SimpleCursorAdapter#setViewImage(android.widget.ImageView, java.lang.String) */
    @Override
    public void setViewImage(ImageView v, String val) {
        v.setImageResource(
            ((null != val) && (0 < val.length()))
            ? R.drawable.present
            : R.drawable.absent);
    }
}
