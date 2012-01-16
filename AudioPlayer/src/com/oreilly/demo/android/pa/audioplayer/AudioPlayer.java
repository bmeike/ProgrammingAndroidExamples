package com.oreilly.demo.android.pa.audioplayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class AudioPlayer extends Activity implements OnSeekBarChangeListener, Runnable, OnCompletionListener {
	public static final String AUDIOFILEURI		= "AudioFileUri";
	public static final String AUDIOFILEPATH	= "AudioFilePath";

	private int state							= 3; // 0 - stop, 1 - play, 2 - pause, 3 - pre state
	private MediaPlayer mediaplayer;
	private Uri mediaReference;
	private String mediaFilePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getIntent() != null) {
			if(getIntent().hasExtra(AUDIOFILEURI))
				mediaReference = getIntent().getParcelableExtra(AUDIOFILEURI);
			else if(getIntent().hasExtra(AUDIOFILEPATH))
				mediaFilePath = getIntent().getStringExtra(AUDIOFILEPATH);
		}

		setUpView();

		initializeMediaPlayer();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		state = 0;
		stopPlayer();
		mediaplayer.release();
	}

	/////////////////////
	// Set up the view //
	/////////////////////
	private void setUpView() {
		setContentView(R.layout.audioplayer);

		findViewById(R.id.seek).setVisibility(View.VISIBLE);

		ImageButton playpauseButton = (ImageButton) findViewById(R.id.playpause);
		playpauseButton.setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				switch(state) {
					case 1: setState(2); break;  // is playing, set to pause
					case 0:						 // is stopped, set to play
					case 3:						 // is in pre state, set to play
					case 2: setState(1); break;  // is paused, set to play
				}
			}

		});

		ImageButton stopButton = (ImageButton) findViewById(R.id.stop);
		stopButton.setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				setState(0);
			}

		});

		SeekBar seek = (SeekBar) findViewById(R.id.seek);
		seek.setOnSeekBarChangeListener(this);
		seek.setProgress(0);
	}

	private void setState(int state) {
		int previousState = this.state;
		this.state = state;

		ImageButton playpauseButton = (ImageButton) findViewById(R.id.playpause);

		switch(state) {
			case 0:
				playpauseButton.setImageResource(R.drawable.play);
				stopPlayer();
				break;
			case 1:
				playpauseButton.setImageResource(R.drawable.pause);
				playPlayer(previousState);
				break;
			case 2:
				playpauseButton.setImageResource(R.drawable.play);
				pausePlayer();
				break;
			default: break;
		}
	}

	//////////////////////////////
	// SeekBar Listener methods //
	//////////////////////////////
	private int seekbarprogress = 0;

	@Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if(fromUser) {
			seekbarprogress = progress;
		}
	}

	@Override
    public void onStartTrackingTouch(SeekBar seekBar) {
		seekbarprogress = 0;
		if(state == 1) pausePlayer();
	}

	@Override
    public void onStopTrackingTouch(SeekBar seekBar) {
		mediaplayer.seekTo(seekbarprogress * 100);
		if(state == 1) playPlayer(2);
		seekbarprogress = 0;
	}

	private void resetSeekBar() {
		int duration = mediaplayer.getDuration();
		SeekBar seek = (SeekBar) findViewById(R.id.seek);
		seek.setMax(duration/100);
		seek.setProgress(0);
	}

	private final Handler seekbarHandler = new Handler() {
		@Override
        public void handleMessage(Message msg) {
			SeekBar seek = (SeekBar) findViewById(R.id.seek);
			seek.setProgress(msg.what);
		}
	};

	@Override
    public void run() {
		try {
			while(state > 0) {
				Thread.sleep(100);  // 10th of a second
				if(seekbarprogress == 0) seekbarHandler.sendEmptyMessage(mediaplayer.getCurrentPosition()/100);
			}
		} catch (Exception e) {}
	}

	/////////////////////////////////
	// MediaPlayer related methods //
	/////////////////////////////////
	private void initializeMediaPlayer() {
		try {
			if(mediaReference == null && mediaFilePath == null) {
				mediaplayer = MediaPlayer.create(this, R.raw.example);
				mediaplayer.setOnCompletionListener(this);
			} else {
				mediaplayer = new MediaPlayer();
				if(mediaReference != null) mediaplayer.setDataSource(this, mediaReference);
				else mediaplayer.setDataSource(mediaFilePath);
				mediaplayer.prepare();
			}

			setState(3);

			resetSeekBar();

			(new Thread(this)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
    public void onCompletion(MediaPlayer mp) {
		setState(0);
	}

	private void playPlayer(int previousState) {
		if(previousState == 0) {
			try {
				mediaplayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			resetSeekBar();

			(new Thread(this)).start();
		}
		mediaplayer.start();
	}

	private void stopPlayer() {
		try { mediaplayer.stop(); } catch (Throwable t) {}
		try { mediaplayer.reset(); } catch (Throwable t) {}
		initializeMediaPlayer();
	}

	private void pausePlayer() {
		mediaplayer.pause();
	}
}
