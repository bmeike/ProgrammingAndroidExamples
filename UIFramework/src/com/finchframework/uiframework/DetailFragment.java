package com.finchframework.uiframework;

import com.finchframework.uiframework.TabManager.SetData;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

public class DetailFragment extends Fragment implements TabListener, SetData {
	
	// String for logging the class name
	private final String TAG = getClass().getSimpleName();
	
	//Turn logging on or off
	private final boolean L = true;
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Notification that the fragment is associated with an Activity
		if (L) Log.i(TAG, "onAttach " + activity.getClass().getSimpleName());
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FrameLayout content = (FrameLayout) inflater.inflate(R.layout.detail, container, false);
		if (L) Log.i(TAG, "onCreateView");
		return content;
		
	}

	public void onStart() {
		super.onStart();
		Log.i(TAG, "onStart");
	}
	
	public void onresume() {
		super.onResume();
		Log.i(TAG, "onResume");
	}
	
	public void onPause() {
		super.onPause();
		Log.i(TAG, "onPause");
	}
	
	public void onStop() {
		super.onStop();
		Log.i(TAG, "onStop");
	}
	
	public void onDestroyView() {
		super.onDestroyView();
		Log.i(TAG, "onDestroyView");
	}
	
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
	}
	
	public void onDetach() {
		super.onDetach();
		Log.i(TAG, "onDetach");
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Minor lifecycle methods 
	//////////////////////////////////////////////////////////////////////////////
	
	public void onActivityCreated() {
		// Notification that the containing activity and its View hierarchy exist
		Log.i(TAG, "onActivityCreated");
	}
	
	///////////////////////////////////////////////////////////////////////////////
	// Overrides of the implementations ComponentCallbacks methods in Fragment
	///////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onConfigurationChanged(Configuration newConfiguration) {
		super.onConfigurationChanged(newConfiguration);
		
		// This won't happen unless we declare changes we handle in the manifest
		if (L) Log.i(TAG, "onConfigurationChanged");
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (L) Log.i(TAG, "onLowMemory");
	}
	
	///////////////////////////////////////////////////////////////////////////////
	// Implementation of TabListener
	///////////////////////////////////////////////////////////////////////////////

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// Do nothing
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.show(this);
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.hide(this);
		
	}
	
	///////////////////////////////////////////////////////////////////////////////
	// Implementation of SetData
	///////////////////////////////////////////////////////////////////////////////

	@Override
	public void setData(Bundle data) {
		// Display the string spelling out the number
		EditText t = (EditText) getActivity().findViewById(R.id.detail_text);
		String s = data.getString("placeName");
		t.setText(s);		
	}
}
