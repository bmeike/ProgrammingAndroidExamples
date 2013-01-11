package com.oreilly.demo.android.pa.audioplayer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

public class VideoRecorderViaIntent extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		recordVideo();
	}
	
	private void recordVideo() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		startActivityForResult(intent, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if(resultCode == RESULT_OK) playVideo(data.getData());
		}
	}
	
	private void playVideo(Uri video) {
		finish();
		if(video == null) return;
		
		Intent intent = new Intent(this, VideoPlayer.class);
		intent.putExtra(VideoPlayer.VIDEOURI, video);
		startActivity(intent);
	}
}
