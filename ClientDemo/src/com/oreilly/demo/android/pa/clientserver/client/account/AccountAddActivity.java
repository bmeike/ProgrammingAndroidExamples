package com.oreilly.demo.android.pa.clientserver.client.account;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.oreilly.demo.android.pa.clientserver.client.R;

public class AccountAddActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountadd);

        setUpView();
    }

    private void setUpView() {
    	findViewById(R.id.add).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				add();
			}
    	});

    	findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
    	});
    }

    private void add() {
    	EditText name = (EditText) findViewById(R.id.name);
    	EditText phone = (EditText) findViewById(R.id.phone);

    	if(name.getText() == null || name.getText().toString().trim().length() < 1) {
    		Toast.makeText(this, "Contacts must have a name", Toast.LENGTH_SHORT);
    		return;
    	}

    	String accountname = null;
    	String accounttype = null;


    	// we are not assigning to any particular account but if we wish to we would
    	// get the accounts this way
    	/*
    	 	Account[] accounts = AccountManager.get(this).getAccounts();
    		accountname = accounts[0].name;
    		accounttype = accounts[0].type;
    	*/

    	ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

    	ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, accountname)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, accounttype)
                .build());

    	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    			.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    			.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
    	        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name.getText().toString().trim())
    	        .build());

    	// if there is a phone num we add it
    	if(phone.getText() != null && phone.getText().toString().trim().length() > 0) {
    		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.getText().toString().trim())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
    	}

    	try {
			getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Something bad happened! "+e.getMessage(), Toast.LENGTH_SHORT);
			return;
		}

    	finish();
    }
}