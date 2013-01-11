/* $Id: $
 */
package com.oreilly.demo.android.pa.contactviewer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:bmeike@callmeike.net">Blake Meike</a>
 */
public class ContactsFragment extends ListFragment {
    private static final String[] CONTACTS_PROJECTION = new String[] {
            BaseColumns._ID,
            Contacts.CONTACT_PRESENCE,
            Contacts.DISPLAY_NAME
        };

    private static final String CONTACTS_FILTER
        = "((" + Contacts.DISPLAY_NAME + " NOT NULL)"
            + " AND (" + Contacts.DISPLAY_NAME + " != ''))";

    private static final String CONTACTS_SORT
        = Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";


    /** @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle) */
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle b)
    {
        View view = super.onCreateView(inflater, container, b);

        installListAdapter(getActivity());

        return view;
    }

    /**
     * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    public void onListItemClick(ListView l, View v, int pos, long row){
        Cursor cursor = (Cursor) getListAdapter().getItem(pos);
        Log.d("####", "Got click at: " + pos);

        String id = cursor.getString(
            cursor.getColumnIndex(BaseColumns._ID));
        String name = cursor.getString(
            cursor.getColumnIndex(Contacts.DISPLAY_NAME));

        Intent intent = new Intent();
        intent.setClass(getActivity(), ContactDetailActivity.class);
        intent.putExtra(ContactDetails.TAG_ID, id);
        intent.putExtra(ContactDetails.TAG_CONTACT, name);
        startActivity(intent);
    }

    private void installListAdapter(Activity activity) {
        setListAdapter(
            new ContactsCursorAdapter(
                activity,
                activity.managedQuery(
                    Contacts.CONTENT_URI,
                    CONTACTS_PROJECTION,
                    CONTACTS_FILTER,
                    null,
                    CONTACTS_SORT)));
    }
}
