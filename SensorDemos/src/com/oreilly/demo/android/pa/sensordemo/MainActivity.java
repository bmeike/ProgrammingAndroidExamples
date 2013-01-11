package com.oreilly.demo.android.pa.sensordemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.sensorsaccel).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), SensorAccel.class));  // Launch the Sensor Accelerometer Example
			}

		});

        findViewById(R.id.sensorgyroscope).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), SensorGyro.class));  // Launch the Sensor Gyroscope Example
			}

		});

       findViewById(R.id.sensorrotate).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), SensorRotationVector.class));  // Launch the Sensor Rotational Vector Example
			}

		});

		findViewById(R.id.sensorlinear).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), SensorRotationVector.class));  // Launch the Sensor Linear Acceleration Example
			}

		});

		findViewById(R.id.sensorgravity).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), SensorGravity.class));  // Launch the Sensor Gravity Example
			}

		});

        findViewById(R.id.sensorlight).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), SensorLight.class));  // Launch the Sensor Light Example
			}

		});

        findViewById(R.id.sensormagnetic).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), SensorMagnetic.class));  // Launch the Sensor Magnetic Example
			}

		});

        findViewById(R.id.sensorpressure).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), SensorPressure.class));  // Launch the Sensor Pressure Example
			}

		});

        findViewById(R.id.sensorproximity).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), SensorProximity.class));  // Launch the Sensor Proximity Example
			}

		});

        findViewById(R.id.sensortemp).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), SensorTemp.class));  // Launch the Sensor Temperature Example
			}

		});

		findViewById(R.id.gesture).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), GestureView.class));  // Launch the Gesture Input Example
			}

		});

		findViewById(R.id.nfc233).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), NFC233.class));  // Launch the NFC 2.3.3 (SDK 10) Example
			}

		});
		
		findViewById(R.id.nfc40).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), NFC40.class));  // Launch the NFC 4.0 (SDK 14) Example
			}

		});
    }
}