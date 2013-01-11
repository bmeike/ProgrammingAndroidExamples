package com.oreilly.demo.android.pa.ndkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NDKApp extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.sample).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), SampleActivityWithNativeMethods.class));
			}

		});

		findViewById(R.id.nativeactivity).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), android.app.NativeActivity.class));
			}

		});
    }
}