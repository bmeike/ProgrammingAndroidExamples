package com.finchframework.uiframework;

import com.finchframework.uiframework.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

/**
 * @author zigurd
 *
 */
public class ListAndContentActivity extends Activity {
	
	// String for logging the class name
	private final String TAG = getClass().getSimpleName();
	
	// Turn logging on or off
	private final boolean L = true;
		
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);

		// To keep this method simple
		doCreate(savedState);
		
		// If we had state to restore, we note that in the log message
		if (L) Log.i(TAG, "onCreate" + 
				(null == savedState ? " Restored state" : ""));
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// Notification that the activity will be started
		if (L) Log.i(TAG, "onRestart");
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Notification that the activity is starting
		if (L) Log.i(TAG, "onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Notification that the activity will interact with the user
		if (L) Log.i(TAG, "onResume");
	}

	protected void onPause() {
		super.onPause();
		// Notification that the activity will stop interacting with the user
		if (L) Log.i(TAG, "onPause" + (isFinishing() ? " Finishing" : ""));
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Notification that the activity is no longer visible
		if (L) Log.i(TAG, "onStop");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Notification the activity will be destroyed
		if (L) Log.i(TAG, "onDestroy"
				// Are we finishing?
				+ (isFinishing() ? " Finishing" : ""));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState(outState);	

		// Called when state should be saved
		if (L) Log.i(TAG, "onSaveInstanceState");

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		if (null != savedState) restoreState(savedState);
		
		// If we had state to restore, we note that in the log message
		if (L) Log.i(TAG, "onRestoreInstanceState" + 
				(null == savedState ? " Restored state" : ""));
	}

	///////////////////////////////////////////////////////////////////////////////
	// The minor lifecycle methods - you probably won't need these
	///////////////////////////////////////////////////////////////////////////////

	@Override
	protected void onPostCreate(Bundle savedState) {
		super.onPostCreate(savedState);
		if (null != savedState) restoreState(savedState);
		
		// If we had state to restore, we note that in the log message
		if (L) Log.i(TAG, "onCreate" + (null == savedState ? " Restored state" : ""));

	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		// Notification that resuming the activity is complete
		if (L) Log.i(TAG, "onPostResume");
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		// Notification that user navigated away from this activity
		if (L) Log.i(TAG, "onUserLeaveHint");
	}
	
	///////////////////////////////////////////////////////////////////////////////
	// Overrides of the implementations ComponentCallbacks methods in Activity
	///////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onConfigurationChanged(Configuration newConfiguration) {
		super.onConfigurationChanged(newConfiguration);
		
		// This won't happen unless we declare changes we handle in the manifest
		if (L) Log.i(TAG, "onConfigurationChanged");
	}
	
	@Override
	public void onLowMemory() {
		// No guarantee this is called before or after other callbacks
		if (L) Log.i(TAG, "onLowMemory");
	}
    
	///////////////////////////////////////////////////////////////////////////////
	// App-specific code here
	///////////////////////////////////////////////////////////////////////////////

	
	/**
	 * This is where we restore state we previously saved.
	 * @param savedState the Bundle we got from the callback
	 */
	private void restoreState(Bundle savedState) {
		// Add your code to restore state here 
		
	}
	
	/**
	 * Add this activity's state to the bundle and/or commit pending data
	 */
	private void saveState(Bundle state) {
		// Add your code to add state to the bundle here
	}
	
	/**
	 * Perform initializations on creation of this Activity instance
	 * @param savedState
	 */
	private void doCreate(Bundle savedState) {
		setContentView(R.layout.main);
        
		if (null != savedState) restoreState(savedState);
		
        ActionBar bar = getActionBar();
        bar.setDisplayShowTitleEnabled(false);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Initialize the tabs (Fails silently if the tab fragments don't exist)
		int names[] = {R.string.content, R.string.detail };
		int fragments[] = { R.id.content_frag, R.id.detail_frag };
		TabManager.initialize(this, 0, names, fragments);
	}

}