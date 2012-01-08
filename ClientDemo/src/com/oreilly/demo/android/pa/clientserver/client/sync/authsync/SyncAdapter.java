package com.oreilly.demo.android.pa.clientserver.client.sync.authsync;

import java.util.ArrayList;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;

import com.oreilly.demo.android.pa.clientserver.client.R;
import com.oreilly.demo.android.pa.clientserver.client.sync.NetworkUtil;
import com.oreilly.demo.android.pa.clientserver.client.sync.dataobjects.Change;
import com.oreilly.demo.android.pa.clientserver.client.sync.dataobjects.ListFriends;
import com.oreilly.demo.android.pa.clientserver.client.sync.dataobjects.User;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private final Context context;

    private static long lastsynctime = 0;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String authtoken = null;
         try {
             authtoken = AccountManager.get(context).blockingGetAuthToken(account, Authenticator.AUTHTOKEN_TYPE, true);

             ListFriends friendsdata = ListFriends.fromJSON(NetworkUtil.getFriends(NetworkUtil.hosturl != null ? NetworkUtil.hosturl : context.getString(R.string.baseurl), authtoken, lastsynctime, null));

             lastsynctime = friendsdata.time;

             sync(account, friendsdata);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    private void sync(Account account, ListFriends data) {
    	User self = new User();
    	self.username = account.name;

    	ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

    	if(data.history != null && !data.history.isEmpty()) {
    		for(Change change : data.history) {
    			if(change.type == Change.ChangeType.DELETE) {
    				ContentProviderOperation op = delete(account, change.who);
    				if(op != null) ops.add(op);
    			}
    		}
    	}

    	if(data.friends != null && !data.friends.isEmpty()) {
    		for(User f : data.friends) {
    			ArrayList<ContentProviderOperation> op = add(account, f);
    			if(op != null) ops.addAll(op);
    		}
    	}

    	if(!ops.isEmpty()) {
	    	try {
	    		context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
    	}
    }

    private ArrayList<ContentProviderOperation> add(Account account, User f) {
    	long rawid = lookupRawContact(f.id);

    	if(rawid != 0) return null;
    	ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
    	ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
    			.withValue(RawContacts.SOURCE_ID, 0)
    			.withValue(RawContacts.SYNC1, f.id)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, Authenticator.ACCOUNT_TYPE)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, account.name)
                .build());

    	if(f.name != null && f.name.trim().length() > 0) {
    		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    			.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    			.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
    	        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, f.name)
    	        .build());
    	}

    	if(f.phone != null && f.phone.trim().length() > 0) {
    		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, f.phone)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
    	}

    	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    			.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    			.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.com.oreilly.demo.android.pa.clientserver.sync.profile")
    			.withValue(ContactsContract.Data.DATA2, "Ch15 Profile")
    			.withValue(ContactsContract.Data.DATA3, "View profile")
    			.build()
    			);
    	return ops;
    }

    private ContentProviderOperation delete(Account account, long id) {
    	long rawid = lookupRawContact(id);
    	if(rawid == 0) return null;
    	return ContentProviderOperation.newDelete(
    			ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawid))
    			.build();
    }

    private long lookupRawContact(long id) {
        long rawid = 0;
        Cursor c = context.getContentResolver().query(RawContacts.CONTENT_URI, new String[] {RawContacts._ID},
        		RawContacts.ACCOUNT_TYPE + "='" + Authenticator.ACCOUNT_TYPE + "' AND "+ RawContacts.SYNC1 + "=?",
        		new String[] {String.valueOf(id)},
                null);
        try {
            if(c.moveToFirst()) {
            	rawid = c.getLong(0);
            }
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
        return rawid;
    }
}
