package com.oreilly.demo.android.pa.ndkdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SampleActivityWithNativeMethods extends Activity {
	static {
        System.loadLibrary("sample");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);

        setupview();
    }

    public native String whatAmI();

    public native double calculatePower(double x, double y);

    private void setupview() {
    	 findViewById(R.id.whatami).setOnClickListener(new View.OnClickListener() {

 			public void onClick(View v) {
 				String whatami = whatAmI();
 				Toast.makeText(SampleActivityWithNativeMethods.this, "CPU: "+whatami, Toast.LENGTH_SHORT).show();
 			}
    	 });

    	 findViewById(R.id.calculate).setOnClickListener(new View.OnClickListener() {

  			public void onClick(View v) {
  				String answer = "";
  				double x = 2;
  				double y = 2;

  				String sx = ((EditText) findViewById(R.id.x)).getText().toString();
  				String sy = ((EditText) findViewById(R.id.y)).getText().toString();

  				if(sx == null) {
  					answer = "X defaults to 2\n";
  				} else {
  					try {
  						x = Double.parseDouble(sx);
  					} catch (Exception e) {
  						answer = "X is not a number, defaulting to 2\n";
  						x = 2;
  					}
  				}

  				if(sy == null) {
  					answer += "Y defaults to 2\n";
  				} else {
  					try {
  						y = Double.parseDouble(sy);
  					} catch (Exception e) {
  						answer = "Y is not a number, defaulting to 2\n";
  						y = 2;
  					}
  				}

  				double z = calculatePower(x, y);

  				answer += x+"^"+y+" = "+z;

  				Toast.makeText(SampleActivityWithNativeMethods.this, answer, Toast.LENGTH_SHORT).show();
  			}
     	 });
    }
}