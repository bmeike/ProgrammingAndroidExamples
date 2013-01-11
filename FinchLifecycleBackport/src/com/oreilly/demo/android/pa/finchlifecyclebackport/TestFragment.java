package com.oreilly.demo.android.pa.finchlifecyclebackport;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TestFragment extends Fragment {

	// get a label for our log entries
	private final String TAG = this.getClass().getSimpleName();

	public TestFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i(TAG, "onAttach");
	}

	@Override
	public void onCreate(Bundle saved) {
		super.onCreate(saved);
		if (null != saved) {
			// Restore state here
		}
		Log.i(TAG, "onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		View v = inflater.inflate(R.layout.fragment_content, container, false);
		Log.i(TAG, "onCreateView");
		return v;
	}

	@Override
	public void onActivityCreated(Bundle saved) {
		super.onActivityCreated(saved);
		Log.i(TAG, "onActivityCreated");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i(TAG, "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, "onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i(TAG, "onStop");
	}

	// ////////////////////////////////////////////////////////////////////////////
	// Called during the lifecycle, when instance state should be saved/restored
	// ////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSaveInstanceState(Bundle toSave) {
		super.onSaveInstanceState(toSave);
		Log.i(TAG, "onSaveinstanceState");
	}
}
