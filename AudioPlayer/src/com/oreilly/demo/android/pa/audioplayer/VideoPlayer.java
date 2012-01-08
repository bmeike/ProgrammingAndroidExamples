package com.oreilly.demo.android.pa.audioplayer;

import java.io.File;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayer extends Activity {
	public static final String VIDEOURI			= "VideoUri";
	public static final String VIDEOFILEPATH	= "VideoFilePath";
	private static final String MEDIAFILE		= "/sdcard/videorecordexample.mp4";
	private static final String SAMPLEURI		= null;

	private Uri videouri;
	private String videopath;
	private MediaController mediacontroller;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getIntent() != null) {
			if(getIntent().hasExtra(VIDEOURI)) {
				videouri = getIntent().getParcelableExtra(VIDEOURI);
			} else if(getIntent().hasExtra(VIDEOFILEPATH)) {
				videopath = getIntent().getStringExtra(VIDEOFILEPATH);
			}
		}

		if(videouri == null && videopath == null) {
			if(SAMPLEURI != null) videouri = Uri.parse(SAMPLEURI);
			else {
				videopath = MEDIAFILE;
				if(!(new File(videopath).exists())) {
					Toast.makeText(this, "Please run the VideoRecorder example first and record a video", Toast.LENGTH_SHORT).show();
					finish();
					return;
				}
			}
		}

		setContentView(R.layout.videoplayer);

		setupVideoView();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mediacontroller != null) mediacontroller.show(0);
		return super.onTouchEvent(event);
	}

	private void setupVideoView() {
		VideoView videoview = (VideoView) findViewById(R.id.videoview);
		videoview.setKeepScreenOn(true);
		if(videouri != null) videoview.setVideoURI(videouri);
		else videoview.setVideoPath(videopath);
		mediacontroller = new MediaController(this);
		mediacontroller.setAnchorView(videoview);
		videoview.setMediaController(mediacontroller);
		if (videoview.canSeekForward())
			videoview.seekTo(videoview.getDuration()/2);
		videoview.start();
	}
}
