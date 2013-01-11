package com.oreilly.demo.android.pa.clientserver.client.sync;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oreilly.demo.android.pa.clientserver.client.R;

public class Settings extends Activity {
	private final Handler saveHandler 				= new Handler() { public void handleMessage(Message msg) { save(); }};

	public final Button.OnClickListener save = new Button.OnClickListener() {
        public void onClick(View v) {
        	saveHandler.sendEmptyMessage(0);
        }
	};

	@Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.settings);

	     setup();
	 }

	 private void setup() {
		 ((TextView) findViewById(R.id.host)).setText(NetworkUtil.hosturl != null ? NetworkUtil.hosturl : getString(R.string.baseurl));

		 findViewById(R.id.save).setOnClickListener(save);
	 }

	 private void save() {
		 Editable e = ((EditText) findViewById(R.id.host)).getText();
		 if(e == null || e.toString() == null || e.toString().trim().length() == 0) NetworkUtil.hosturl = null;
		 else NetworkUtil.hosturl = e.toString().trim();

		 finish();
	 }
}
