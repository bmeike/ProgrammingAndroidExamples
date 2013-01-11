package com.oreilly.demo.android.pa.audioplayer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class AudioPlayerWAudioTrack extends Activity {
    private static final String MEDIAFILE        = "/sdcard/audiorecordwaudiorecordexample.pcm";
    public static final String AUDIOFILEPATH    = "AudioFilePath";

    private int state                            = 3; // 0 - stop, 1 - play, 2 - pause, 3 - pre state
    private AudioTrack audiotrack;
    private short[] audio;
    private String mediaFilePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent() != null && getIntent().hasExtra(AUDIOFILEPATH)) {
            mediaFilePath = getIntent().getStringExtra(AUDIOFILEPATH);
        } else {
            mediaFilePath = MEDIAFILE;
        }

        if(!(new File(mediaFilePath).exists())) {
            Toast.makeText(this, "Please run the AudioRecorder W AudioRecord example first and record an audio file", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setUpView();

        initializeAudioTrack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        state = 0;
        stopPlayer(false);
    }

    /////////////////////
    // Set up the view //
    /////////////////////
    private void setUpView() {
        setContentView(R.layout.audioplayer);

        ImageButton playpauseButton = (ImageButton) findViewById(R.id.playpause);
        playpauseButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch(state) {
                    case 1: setState(2); break;  // is playing, set to pause
                    case 0:                         // is stopped, set to play
                    case 3:                         // is in pre state, set to play
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
    }

    private void setState(int state) {
        int previousState = this.state;
        this.state = state;

        ImageButton playpauseButton = (ImageButton) findViewById(R.id.playpause);

        switch(state) {
            case 0:
                playpauseButton.setImageResource(R.drawable.play);
                stopPlayer(true);
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

    /////////////////////////////////
    // AudioTrack related methods //
    /////////////////////////////////
    private void initializeAudioTrack() {
        try {
            File mediafile = new File(mediaFilePath);
            if(!mediafile.exists()) {
                throw new Exception(mediaFilePath+" does not exist");
            }

            audio = new short[(int) (mediafile.length()/2)];

            // read in file
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(mediafile)));

            int i = 0;
            while(dis.available() > 0) {
                audio[i] = dis.readShort();
                i++;
            }

            dis.close();

            // create AudioTrack
            audiotrack = new AudioTrack(
                                AudioManager.STREAM_MUSIC,                    // stream type
                                44100,                                        // frequency
                                AudioFormat.CHANNEL_CONFIGURATION_MONO,       // channel config.. mono, stereo, etc
                                AudioFormat.ENCODING_PCM_16BIT,               // audio encoding
                                audio.length,                                 // length
                                AudioTrack.MODE_STREAM                        // mode
                                );

            setState(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCompletion() {
        setState(0);
    }

    private void playPlayer(int previousState) {
        (new Thread() {
            @Override
            public void run() {
                audiotrack.play();
                audiotrack.write(audio, 0, audio.length);
            }
        }).start();
    }

    private void stopPlayer(boolean reinit) {
        if(audiotrack != null) {
            try { audiotrack.stop(); } catch(Exception e) { e.printStackTrace(); }
            audiotrack.release();
            audiotrack = null;
            audio = null;
        }
        if(reinit) {
            initializeAudioTrack();
        }
    }

    private void pausePlayer() {
        if(audiotrack != null) {
            audiotrack.pause();
        }
    }
}
