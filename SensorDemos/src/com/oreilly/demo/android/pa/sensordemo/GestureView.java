package com.oreilly.demo.android.pa.sensordemo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class GestureView extends Activity implements GestureOverlayView.OnGesturePerformedListener {
	private GestureLibrary library;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture);

        setTitle("Gesture");

        findViewById(R.id.add).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(GestureView.this, GestureAdd.class));
			}
        });
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateGestureLibrary();

		GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.addOnGesturePerformedListener(this);
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		ArrayList<Prediction> predictions = library.recognize(gesture);
		if (predictions.size() > 0) {
			Prediction prediction = predictions.get(0);
			if (prediction.score > 1.0) {
				Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void updateGestureLibrary() {
		library = GestureLibraries.fromFile("/sdcard/gestureexample");
		library.load();
	}
}
