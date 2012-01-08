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

public class SensorMagnetic extends Activity implements SensorEventListener {

	private boolean hassensor;

	private final Handler magneticEventHandler 				= new Handler() {
																	@Override
																	public void handleMessage(Message msg) {
																		Bundle data = msg.getData();
																		((TextView) findViewById(R.id.magxtext)).setText(data.getString("x"));
																		((TextView) findViewById(R.id.magytext)).setText(data.getString("y"));
																		((TextView) findViewById(R.id.magztext)).setText(data.getString("z"));
																	}
																};

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getMagneticSensors() == null) {
			hassensor = false;
			Toast.makeText(this, "No Magnetic Sensors Available", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		hassensor = true;
		setContentView(R.layout.sensormagnetic);

		setTitle("Magnetic");
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

	private List<Sensor> getMagneticSensors() {
		SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = mngr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		return list != null && !list.isEmpty() ? list : null;
	}

	private void registerListener() {
		SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = getMagneticSensors();
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
		float magx = event.values[0];
		float magy = event.values.length > 1 ? event.values[1] : 0;
		float magz = event.values.length > 2 ? event.values[2] : 0;

		Bundle data = new Bundle();
		data.putString("x", "Mag X: "+magx+" uT");
		data.putString("y", "Mag Y: "+magy+" uT");
		data.putString("z", "Mag Z: "+magz+" uT");
		Message msg = Message.obtain();
		msg.setData(data);
		magneticEventHandler.sendMessage(msg);
	}
}
