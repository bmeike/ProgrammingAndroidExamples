package com.oreilly.demo.android.pa.sensordemo;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class GestureAdd extends Activity implements GestureOverlayView.OnGesturePerformedListener {
	private GestureLibrary library;
	private Gesture gesture;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestureadd);

        setTitle("Add Gesture");

        findViewById(R.id.save).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(storeGesture()) finish();
			}
        });

        findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
        });
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateGestureLibrary();

		GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
		gestures.setFadeEnabled(false);
        gestures.addOnGesturePerformedListener(this);
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		this.gesture = gesture;
	}

	private void updateGestureLibrary() {
		library = GestureLibraries.fromFile("/sdcard/gestureexample");
		library.load();
	}

	private boolean storeGesture() {
		if(gesture != null) {
			String name = null;
			if(((EditText) findViewById(R.id.name)).getText() != null) {
				name = ((EditText) findViewById(R.id.name)).getText().toString();
			}
			if(name == null || name.trim().length() < 1) {
				Toast.makeText(this, "You must provide a name", Toast.LENGTH_SHORT).show();
				return false;
			}
			library.addGesture(name, gesture);
			library.save();
			return true;
		} else Toast.makeText(this, "You must make a gesture", Toast.LENGTH_SHORT).show();
		return false;
	}
}
