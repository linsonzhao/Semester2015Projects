package video.app.com;

import video.app.com.objects.Video;
import video.app.com.util.Util;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.exemplo.videoapp.R;

public class PlayVideo extends SherlockActivity {
	
	private VideoView vView;

	Util util;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showvideo);
		getSupportActionBar().setTitle(getVideo().getName());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				
		init();
		
	}
	
	private void init() {		
		util = new Util(PlayVideo.this);
		
		/**
		 * Check if the video is MP4. If it's play on the app, otherwise,
		 * launches an Intent and the user may select how to play it (or even just download 
		 * it to his phone), because not every format is playable on Android, such as MPG.
		 */
		if(getVideo().getVideoFile().endsWith(".mp4")) {
			vView = (VideoView) findViewById(R.id.videoView1);
			vView.setKeepScreenOn(true);
			Log.i("PlayVideo", "Trying to play : " + util.getURL() + getVideo().getVideoFile());
			vView.setVideoURI(Uri.parse(util.getURL() + getVideo().getVideoFile()));
			vView.setMediaController(new MediaController(this));
			vView.requestFocus();
			vView.start();
		} else
			startActivity(new Intent(Intent.ACTION_VIEW)
				.setData(Uri.parse(util.getURL() + getVideo().getVideoFile())));
		
	}

	private Video getVideo() {
		return (Video) getIntent().getExtras().getParcelable("Video");
	}
		
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;			
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
}
