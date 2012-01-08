package com.oreilly.demo.android.pa.clientserver.client.sync.authsync;

import org.json.JSONObject;


import com.oreilly.demo.android.pa.clientserver.client.R;
import com.oreilly.demo.android.pa.clientserver.client.sync.LoginActivity;
import com.oreilly.demo.android.pa.clientserver.client.sync.NetworkUtil;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Authenticator extends AbstractAccountAuthenticator {
	public static final String AUTHTOKEN_TYPE 				= "com.oreilly.demo.android.pa.clientserver.sync";
	public static final String ACCOUNT_TYPE					= "com.oreilly.demo.android.pa.clientserver.sync";

    private final Context context;

    public Authenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
    	if (options != null && options.containsKey(AccountManager.KEY_PASSWORD)) {
            String password = options.getString(AccountManager.KEY_PASSWORD);
            JSONObject json = NetworkUtil.login(NetworkUtil.hosturl != null ? NetworkUtil.hosturl : context.getString(R.string.baseurl), account.name, password, true, null);
            Bundle result = new Bundle();
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, json != null);
            return result;
        }
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.PARAM_USERNAME, account.name);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle loginOptions) {
    	if (!authTokenType.equals(AUTHTOKEN_TYPE)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }
        String password = AccountManager.get(context).getPassword(account);
        if (password != null) {
            JSONObject json = NetworkUtil.login(NetworkUtil.hosturl != null ? NetworkUtil.hosturl : context.getString(R.string.baseurl), account.name, password, true, null);
            if(json != null) {
                Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                result.putString(AccountManager.KEY_AUTHTOKEN, json.optString("token"));
                return result;
            }
        }
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.PARAM_USERNAME, account.name);
        intent.putExtra(LoginActivity.PARAM_AUTHTOKEN_TYPE, ACCOUNT_TYPE);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return authTokenType.equals(AUTHTOKEN_TYPE) ? context.getString(R.string.authlabel): null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) {
    	Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle loginOptions) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.PARAM_USERNAME, account.name);
        intent.putExtra(LoginActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

}
