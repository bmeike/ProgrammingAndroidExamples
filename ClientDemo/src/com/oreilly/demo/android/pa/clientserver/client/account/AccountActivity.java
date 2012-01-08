package com.oreilly.demo.android.pa.clientserver.client.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.oreilly.demo.android.pa.clientserver.client.R;

public class AccountActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        setupView();
    }

    private void setupView() {
    	findViewById(R.id.add).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), AccountAddActivity.class));
			}
		});

    	ListView contactlist = (ListView) findViewById(R.id.contactlist);

    	final Cursor cursor = getContacts();

    	contactlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
													final int position, long id) {
				cursor.moveToPosition(position);
				final String name = cursor.getString(2);
				AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
				builder.setTitle("Delete "+name+"?");
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(dialog != null) dialog.dismiss();
					}
				});
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(dialog != null) dialog.dismiss();
						deleteContact(cursor, position);
						setupView();
					}
				});
				builder.create().show();
			}

    	});


        String[] fields = new String[] {
                ContactsContract.Data.DISPLAY_NAME
        };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
        													R.layout.contact,
        													cursor,
        													fields,
        													new int[] {R.id.name});
        contactlist.setAdapter(adapter);
    }

    private Cursor getContacts() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME
        };

        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
    }

    private void deleteContact(Cursor cursor, int position) {
    	cursor.moveToPosition(position);
    	long id = cursor.getLong(0);
    	String lookupkey = cursor.getString(1);
    	Uri uri = ContactsContract.Contacts.getLookupUri(id, lookupkey);

    	String[] selectionArgs = null;
    	String where = null;
    	ContentResolver cr = getContentResolver();
    	cr.delete(uri, where, selectionArgs);
    }
}