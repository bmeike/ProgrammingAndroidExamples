package com.oreilly.demo.android.pa.clientserver.client.sync;

import org.json.JSONObject;

import com.oreilly.demo.android.pa.clientserver.client.R;
import com.oreilly.demo.android.pa.clientserver.client.sync.authsync.Authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AccountAuthenticatorActivity {
	public static final String PARAM_AUTHTOKEN_TYPE 		= "authtokenType";
	public static final String PARAM_USERNAME 				= "username";
	public static final String PARAM_PASSWORD 				= "password";

	private String username;
	private String password;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getVars();
        setupView();
	}

	@Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Attemping to login");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

	private void getVars() {
        username = getIntent().getStringExtra(PARAM_USERNAME);
	}

	private void setupView() {
		setContentView(R.layout.login);

		findViewById(R.id.login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				login();
			}
		});

		findViewById(R.id.settings).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), Settings.class));
			}
		});

		if(username != null) {
			((EditText) findViewById(R.id.username)).setText(username);
		}
	}

	private void login() {
		if(((EditText) findViewById(R.id.username)).getText() == null ||
				((EditText) findViewById(R.id.username)).getText().toString().trim().length() < 1) {
				Toast.makeText(this, "Please enter a Username", Toast.LENGTH_SHORT).show();
			return;
		}
		if(((EditText) findViewById(R.id.password)).getText() == null ||
				((EditText) findViewById(R.id.password)).getText().toString().trim().length() < 1) {
				Toast.makeText(this, "Please enter a Password", Toast.LENGTH_SHORT).show();
			return;
		}

		username = ((EditText) findViewById(R.id.username)).getText().toString();
		password = ((EditText) findViewById(R.id.password)).getText().toString();

		showDialog(0);

		Handler loginHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == NetworkUtil.ERR) {
					dismissDialog(0);
					Toast.makeText(LoginActivity.this, "Login Failed: "+msg.obj, Toast.LENGTH_SHORT).show();
				} else if(msg.what == NetworkUtil.OK) {
					handleLoginResponse((JSONObject) msg.obj);
				}
			}
		};

		NetworkUtil.login(NetworkUtil.hosturl != null ? NetworkUtil.hosturl : getString(R.string.baseurl), username, password, loginHandler);
	}

	private void handleLoginResponse(JSONObject resp) {
		dismissDialog(0);

		final Account account = new Account(username, Authenticator.ACCOUNT_TYPE);

        if (getIntent().getStringExtra(PARAM_USERNAME) == null) {
        	AccountManager.get(this).addAccountExplicitly(account, password, null);
            ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);
        } else {
        	AccountManager.get(this).setPassword(account, password);
        }

        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Authenticator.ACCOUNT_TYPE);
        if (resp.has("token")) {
            intent.putExtra(AccountManager.KEY_AUTHTOKEN, resp.optString("token"));
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
	}
}
