package com.oreilly.demo.android.pa.audioplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MultiMedia extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		findViewById(R.id.audioplayer).setOnClickListener(new View.OnClickListener() {

			@Override
            public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), AudioPlayer.class));  // Launch the AudioPlayer Example
			}

		});

		findViewById(R.id.audioplayerwmediacontroller).setOnClickListener(new View.OnClickListener() {

			@Override
            public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), AudioPlayerWMediaController.class));  // Launch the AudioPlayer W Media ControllerExample
			}

		});

		findViewById(R.id.audioplayerwaudiotrack).setOnClickListener(new View.OnClickListener() {

			@Override
            public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), AudioPlayerWAudioTrack.class));  // Launch the AudioPlayer W AudioTrack Example
			}

		});

		findViewById(R.id.audiorecorder).setOnClickListener(new View.OnClickListener() {

			@Override
            public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), AudioRecorder.class));  // Launch the AudioRecorder Example
			}

		});

		findViewById(R.id.audiorecorderwaudiorecord).setOnClickListener(new View.OnClickListener() {

			@Override
            public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), AudioRecorderWAudioRecord.class));  // Launch the AudioRecorder W AudioRecord Example
			}

		});

		findViewById(R.id.audiorecorderintent).setOnClickListener(new View.OnClickListener() {

			@Override
            public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), AudioRecorderViaIntent.class));  // Launch the AudioRecorder By Intent Example
			}

		});

		findViewById(R.id.videoplayer).setOnClickListener(new View.OnClickListener() {

			@Override
            public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), VideoPlayer.class));  // Launch the VideoPlayer Example
			}

		});

		findViewById(R.id.videorecorder).setOnClickListener(new View.OnClickListener() {

			@Override
            public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), VideoRecorder.class));  // Launch the VideoRecorder Example
			}

		});

		findViewById(R.id.videorecorderintent).setOnClickListener(new View.OnClickListener() {

			@Override
            public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), VideoRecorderViaIntent.class));  // Launch the VideoRecorder By Intent Example
			}

		});
	}
}
