package com.oreilly.demo.android.pa.audioplayer;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AudioRecorderWAudioRecord extends Activity implements Runnable {
	private static final String MEDIAFILE= "/sdcard/audiorecordwaudiorecordexample.pcm";

	private AudioRecord recorder;
	private DataOutputStream dos;
	private boolean recordablestate = false;
	private int time = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setupView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopRecord();
	}

	private void setupView() {
		setContentView(R.layout.audiorecorder);

		findViewById(R.id.recordstop).setOnClickListener(new OnClickListener() {

			@Override
		    public void onClick(View v) {
				recordOrStop();
			}

		});

		findViewById(R.id.play).setOnClickListener(new OnClickListener() {

			@Override
		    public void onClick(View v) {
				playAudio();
			}

		});
	}

	private void recordOrStop() {
		if(!recordablestate) {
			try {
				recordablestate = true;
				ImageButton button = (ImageButton) findViewById(R.id.recordstop);
				button.setImageResource(R.drawable.stop);
				findViewById(R.id.play).setVisibility(View.GONE);

				File mediafile = new File(MEDIAFILE);
				if(mediafile.exists()) {  // delete mediafile if it already exists
					mediafile.delete();
				}

				// create mediafile
				mediafile.createNewFile();

				// setup DataOutputStream to write to file
				dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mediafile)));

				// setup AudioRecord
				final short[] buffer = new short[10000];

				recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, // source to record from
											44100,// frequency
											AudioFormat.CHANNEL_CONFIGURATION_MONO,// channel config.. mono, stereo, etc
											AudioFormat.ENCODING_PCM_16BIT,// audio encoding
											buffer.length// buffer size
											);

				// start recording in a separate thread
				(new Thread() {
					@Override
				    public void run() {
						if(recorder != null) {
							try {
								recorder.startRecording();
							} catch(IllegalStateException ise) {
								Message.obtain(notifyHandler, 0, "This is best run on a real device.").sendToTarget();
								recorder = null;
								recordablestate = false;
							}
							while(recordablestate) {
								try {
									int readBytes = recorder.read(buffer, 0, buffer.length);  // read in up to buffer size
									for(int i=0;i<readBytes;i++) {
										dos.writeShort(buffer[i]);  // write out to file
									}
								} catch (Exception t) {
									recordablestate = false;
									t.printStackTrace();
								}
							}
							stopRecord();  // stop recording
						}
					}
				}).start();

				(new Thread(this)).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			recordablestate = false;
			ImageButton button = (ImageButton) findViewById(R.id.recordstop);
			button.setImageResource(R.drawable.record);
			findViewById(R.id.play).setVisibility(View.VISIBLE);
		}
	}

	private void stopRecord() {
		recordablestate = false;
		if(recorder != null) {
			try { recorder.stop(); } catch (IllegalStateException ise) { }
			recorder.release();
			recorder = null;
		}
		if(dos != null) {
			try { dos.flush(); } catch (IOException io) { }
			try { dos.close(); } catch (IOException io) { }
			dos = null;
		}
	}

	private final Handler timeupdater = new Handler() {
		@Override
	    public void handleMessage(Message msg) {
			time++;
			((TextView) findViewById(R.id.time)).setText(""+time+" secs");
		}
	};

	@Override
	public void run() {
		try {
			time = 0;
			timeupdater.sendEmptyMessage(0);
			while(recordablestate) {
				Thread.sleep(1000); // per sec
				timeupdater.sendEmptyMessage(0);
			}
		} catch (Exception t) { }
	}

	private void playAudio() {
		finish();

		Intent intent = new Intent(this, AudioPlayerWAudioTrack.class);
		intent.putExtra(AudioPlayer.AUDIOFILEPATH, MEDIAFILE);
		startActivity(intent);
	}

	private final Handler notifyHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.obj != null && msg.obj instanceof String) notifyMsg((String) msg.obj);
		}
	};

	private void notifyMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
