package com.oreilly.demo.android.pa.sensordemo;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.accessibility.AccessibilityEvent;

public class Accessibility extends AccessibilityService implements OnInitListener {
	private static final int STOP						= 0;
	private static final int END						= 1;
	private static final int CLICK						= 2;
	private static final long[] VIBRATEPATTERN			= new long[] { 0L, 100L };
	
	private final Handler accessHandler = new Handler() {
										@Override
										public void handleMessage(Message msg) {
											switch(msg.what) {
												case STOP: stopAccess(); break;
												case END: endAccess(); break;
												case CLICK: doClick((AccessibilityEvent) msg.obj); break;
											}
										}
									};

	private TextToSpeech tts;
	private Vibrator vibrate;
									
	@Override
	protected void onServiceConnected () {
		super.onServiceConnected();
		
		setUpAccessibilityType();
		startServices();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		accessHandler.sendEmptyMessage(END);
		return false;
	}
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if(event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
			Message.obtain(accessHandler, CLICK, event).sendToTarget();
		}
	}

	@Override
	public void onInterrupt() {
		accessHandler.sendEmptyMessage(STOP);
	}
	
	@Override
	public void onInit(int status) { } 

	private void setUpAccessibilityType() {
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
		info.notificationTimeout = 50;
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC | AccessibilityServiceInfo.FEEDBACK_AUDIBLE |
							AccessibilityServiceInfo.FEEDBACK_HAPTIC | AccessibilityServiceInfo.FEEDBACK_SPOKEN |
							AccessibilityServiceInfo.FEEDBACK_VISUAL;
		info.packageNames = new String[1];
		info.packageNames[0] = getPackageName();  // only handle this package
		setServiceInfo(info);
	}
	
	private void startServices() {
		tts = new TextToSpeech(this, this);
		vibrate = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
	}
	
	private void stopAccess() {
		tts.stop();
		vibrate.cancel();
	}
	
	private void endAccess() {
		vibrate.cancel();
		tts.shutdown();
	}
	
	private void doClick(AccessibilityEvent event) {
		vibrate.vibrate(VIBRATEPATTERN, -1);
		tts.speak("Click", 2, null);
	}	
}
