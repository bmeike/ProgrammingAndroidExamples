/* $Id: $
 */
package com.oreilly.demo.android.pa.contactviewer;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


/**
 * ContactDetailsLoader
 * Load contact details from a cursor
 */
class ContactDetailsLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    private final ContactDetails details;
    private final Activity act;
    private final Uri uri;
    private final String sel;
    private final String rec;
    private final String[] from;
    private final int[] to;

    private Cursor cursor;

    public ContactDetailsLoader(
        ContactDetails details,
        Activity act,
        Uri uri,
        String sel,
        String rec,
        String[] from,
        int[] to)
    {
        this.details = details;
        this.act = act;
        this.uri = uri;
        this.sel = sel;
        this.rec = rec;
        this.from = from;
        this.to = to;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
            act,
            uri,
            from,
            sel + " = ?",
            new String[] { rec },
            null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (0 >= data.getCount()) { return; }
        cursor = data;
        populateFields();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursor = null;
    }

    public void populateFields() {
        if (null == cursor) { return; }
        cursor.moveToFirst();
        details.populateFields(cursor, from, to);
    }
}
