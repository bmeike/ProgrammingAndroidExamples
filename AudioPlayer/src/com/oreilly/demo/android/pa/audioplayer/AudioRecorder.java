package com.oreilly.demo.android.pa.audioplayer;

import java.io.File;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AudioRecorder extends Activity implements Runnable {
	private MediaRecorder mediarecorder;
	private static final String MEDIAFILE= "/sdcard/audiorecordexample.3gpp";

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
		recordablestate = false;
		if(mediarecorder != null) mediarecorder.release();
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

		findViewById(R.id.register).setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				registerAudioFile();
			}

		});

	}

	private void recordOrStop() {
		if(!recordablestate) {
			try {
				File mediafile = new File(MEDIAFILE);
				if(mediafile.exists()) {
					mediafile.delete();
				}
				mediafile = null;

				recordablestate = true;
				ImageButton button = (ImageButton) findViewById(R.id.recordstop);
				button.setImageResource(R.drawable.stop);
				findViewById(R.id.play).setVisibility(View.GONE);
				findViewById(R.id.register).setVisibility(View.GONE);

				if(mediarecorder == null) mediarecorder = new MediaRecorder();
				mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				mediarecorder.setOutputFile(MEDIAFILE);
				mediarecorder.prepare();
				mediarecorder.start();

				(new Thread(this)).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			recordablestate = false;
			mediarecorder.stop();
			mediarecorder.reset();
			ImageButton button = (ImageButton) findViewById(R.id.recordstop);
			button.setImageResource(R.drawable.record);
			findViewById(R.id.play).setVisibility(View.VISIBLE);
			findViewById(R.id.register).setVisibility(View.VISIBLE);
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

		Intent intent = new Intent(this, AudioPlayer.class);
		intent.putExtra(AudioPlayer.AUDIOFILEPATH, MEDIAFILE);
		startActivity(intent);
	}

	private void registerAudioFile() {
		ContentValues content = new ContentValues();
		content.put(MediaStore.MediaColumns.DATA, MEDIAFILE);
		content.put(MediaStore.MediaColumns.TITLE, "AudioRecordExample");
		content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/amr");
		content.put(AudioColumns.ARTIST, "Me");
		content.put(AudioColumns.IS_MUSIC, true);
		ContentResolver resolve = getContentResolver();
		Uri uri = resolve.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, content);
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

		Toast.makeText(getBaseContext(), "AudioRecordExample registered with system.", Toast.LENGTH_SHORT).show();

		finish();
	}
}
