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

public class SensorLight extends Activity implements SensorEventListener {

	private boolean hassensor;

	private final Handler lightEventHandler 				= new Handler() {
																	@Override
																	public void handleMessage(Message msg) {
																		String light = (String) msg.obj;
																		((TextView) findViewById(R.id.lighttext)).setText(light);
																	}
																};

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getLightSensors() == null) {
			hassensor = false;
			Toast.makeText(this, "No Light Sensors Available", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		hassensor = true;
		setContentView(R.layout.sensorlight);

		setTitle("Light");
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

	private List<Sensor> getLightSensors() {
		SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = mngr.getSensorList(Sensor.TYPE_LIGHT);
		return list != null && !list.isEmpty() ? list : null;
	}

	private void registerListener() {
		SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = getLightSensors();
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
		float light = event.values[0];  // Ambient light level in SI lux units
		Message msg = Message.obtain();
		msg.obj = "Light: "+light+" lx";
		lightEventHandler.sendMessage(msg);
	}
}
