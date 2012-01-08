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

public class SensorGyro extends Activity implements SensorEventListener {

	private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    private float[] angle;

	private boolean hassensor;

	private final Handler gyroEventHandler 				= new Handler() {
																	@Override
																	public void handleMessage(Message msg) {
																		Bundle data = msg.getData();
																		((TextView) findViewById(R.id.gyroxtext)).setText(data.getString("x"));
																		((TextView) findViewById(R.id.gyroytext)).setText(data.getString("y"));
																		((TextView) findViewById(R.id.gyroztext)).setText(data.getString("z"));
																		((TextView) findViewById(R.id.gyrodxtext)).setText(data.getString("dx"));
																		((TextView) findViewById(R.id.gyrodytext)).setText(data.getString("dy"));
																		((TextView) findViewById(R.id.gyrodztext)).setText(data.getString("dz"));

																	}
																};

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getGyroSensors() == null) {
			hassensor = false;
			Toast.makeText(this, "No Gyroscopic Sensors Available", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		hassensor = true;
		setContentView(R.layout.sensorgyro);

		setTitle("Gyroscope");
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

	private List<Sensor> getGyroSensors() {
		SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = mngr.getSensorList(Sensor.TYPE_GYROSCOPE);
		return list != null && !list.isEmpty() ? list : null;
	}

	private void registerListener() {
		SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = getGyroSensors();
		if(list != null) {
			timestamp = 0;
			angle = new float[3];
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
		float gyrox = event.values[0];
		float gyroy = event.values.length > 1 ? event.values[1] : 0;
		float gyroz = event.values.length > 2 ? event.values[2] : 0;

		if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            angle[0] += gyrox * dT;
            angle[1] += gyroy * dT;
            angle[2] += gyroz * dT;
        }

        timestamp = event.timestamp;

		Bundle data = new Bundle();
		data.putString("dx", "Speed Around X axis: "+gyrox+" rad/sec");
		data.putString("dy", "Speed Around Y axis: "+gyroy+" rad/sec");
		data.putString("dz", "Speed Around Z axis: "+gyroz+" rad/sec");
		data.putString("x", "Around X axis: "+angle[0]+" rad");
		data.putString("y", "Around Y axis: "+angle[1]+" rad");
		data.putString("z", "Around Z axis: "+angle[2]+" rad");
		Message msg = Message.obtain();
		msg.setData(data);
		gyroEventHandler.sendMessage(msg);
	}
}
