package com.oreilly.demo.android.pa.sensordemo;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

public class SensorAccel extends Activity implements SensorEventListener {

	private boolean hassensor;

	private final Handler accelEventHandler 				= new Handler() {
																	@Override
																	public void handleMessage(Message msg) {
																		Bundle data = msg.getData();
																		((TextView) findViewById(R.id.accelxtext)).setText(data.getString("x"));
																		((TextView) findViewById(R.id.accelytext)).setText(data.getString("y"));
																		((TextView) findViewById(R.id.accelztext)).setText(data.getString("z"));
																	}
																};

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getAccelorometer() == null) {
			hassensor = false;
			Toast.makeText(this, "No Accelerometer Available", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		hassensor = true;
		setContentView(R.layout.sensoraccel);
		setTitle("Accelerometer");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(hassensor) registerListener();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterListener();
	}

	private List<Sensor> getAccelorometer() {
		SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = mngr.getSensorList(Sensor.TYPE_ACCELEROMETER);
		return list != null && !list.isEmpty() ? list : null;
	}

	private void registerListener() {
		SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = getAccelorometer();
		if(list != null) {
			for(Sensor sensor: list) {
				mngr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
			}
		}
	}

	private void unregisterListener() {
		if(hassensor) {
			SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			mngr.unregisterListener(this);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }

	@Override
	public void onSensorChanged(SensorEvent event) {
		float ax = event.values[0];
		float ay = event.values.length > 1 ? event.values[1] : 0;
		float az = event.values.length > 2 ? event.values[2] : 0;

		Bundle data = new Bundle();
		data.putString("x", "X: "+ax+" m/s^2");
		data.putString("y", "Y: "+ay+" m/s^2");
		data.putString("z", "Z: "+az+" m/s^2");
		Message msg = Message.obtain();
		msg.setData(data);
		accelEventHandler.sendMessage(msg);
	}
}
