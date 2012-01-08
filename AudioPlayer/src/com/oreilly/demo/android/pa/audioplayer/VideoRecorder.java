package com.oreilly.demo.android.pa.audioplayer;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoRecorder extends Activity implements Runnable, SurfaceHolder.Callback {
	private MediaRecorder mediarecorder;
	private static final String MEDIAFILE= "/sdcard/videorecordexample.mp4";

	private boolean recordablestate = false;
	private int time = 0;
	private VideoView videoview;

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
		setContentView(R.layout.videorecorder);
		findViewById(R.id.recordstop).setEnabled(false);


		videoview = (VideoView) findViewById(R.id.videosurface);
		final SurfaceHolder holder = videoview.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		findViewById(R.id.recordstop).setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				recordOrStop(holder);
			}

		});

		findViewById(R.id.play).setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				playVideo();
			}

		});

	}

	private void recordOrStop(SurfaceHolder holder) {
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

				if(mediarecorder == null) mediarecorder = new MediaRecorder();
				mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
				mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
				mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
				mediarecorder.setOutputFile(MEDIAFILE);
				mediarecorder.setPreviewDisplay(holder.getSurface());
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

	private void playVideo() {
		finish();

		Intent intent = new Intent(this, VideoPlayer.class);
		intent.putExtra(VideoPlayer.VIDEOFILEPATH, MEDIAFILE);
		startActivity(intent);
	}

	@Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
    public void surfaceCreated(SurfaceHolder holder) {
		findViewById(R.id.recordstop).setEnabled(true);
	}

	@Override
    public void surfaceDestroyed(SurfaceHolder holder) {

	}
}
