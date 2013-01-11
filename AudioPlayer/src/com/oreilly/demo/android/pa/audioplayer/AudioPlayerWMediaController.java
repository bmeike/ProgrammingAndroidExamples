package com.oreilly.demo.android.pa.audioplayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.MediaController;

public class AudioPlayerWMediaController extends Activity {
	public static final String AUDIOFILEURI		= "AudioFileUri";

	private MediaPlayer mediaplayer;
	private Uri mediaReference;
	private MediaController controller;
	private boolean begin = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getIntent() != null && getIntent().hasExtra(AUDIOFILEURI))
			mediaReference = getIntent().getParcelableExtra(AUDIOFILEURI);

		setUpView();

		initializeMediaPlayer();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus && begin && !controller.isShowing()) {
			begin = false;
			controller.show(0);
			mediaplayer.start();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mediaplayer.release();
	}

	/////////////////////
	// Set up the view //
	/////////////////////
	private void setUpView() {
		setContentView(R.layout.audioplayerwmediacontrol);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		controller.show(0);
		return super.onTouchEvent(event);
	}

	/////////////////////////////////
	// MediaPlayer related methods //
	/////////////////////////////////
	private void initializeMediaPlayer() {
		try {
			if(mediaReference == null) {
				mediaplayer = MediaPlayer.create(this, R.raw.example);
			} else {
				mediaplayer = new MediaPlayer();
				mediaplayer.setDataSource(this, mediaReference);
				mediaplayer.prepare();
			}

			controller = new MediaController(this);
			controller.setMediaPlayer(new AudioController(mediaplayer));
			controller.setEnabled(true);
			controller.setAnchorView(findViewById(R.id.mainlayout));


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class AudioController implements MediaController.MediaPlayerControl {
		private MediaPlayer media;

		public AudioController(MediaPlayer md) {
			media = md;
		}

		@Override
        public boolean canPause() {
			return true;
		}

		@Override
        public boolean canSeekBackward() {
			return true;
		}

		@Override
        public boolean canSeekForward() {
			return true;
		}

		@Override
        public int getBufferPercentage() {
			return 0;
		}

		@Override
        public int getCurrentPosition() {
			return media.getCurrentPosition();
		}

		@Override
        public int getDuration() {
			return media.getDuration();
		}

		@Override
        public boolean isPlaying() {
			return media.isPlaying();
		}

		@Override
        public void pause() {
			media.pause();
		}

		@Override
        public void seekTo(int pos) {
			media.seekTo(pos);
		}

		@Override
        public void start() {
			media.start();
		}

	}
}
