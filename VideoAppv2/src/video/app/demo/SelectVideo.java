package video.app.demo;

import video.app.demo.util.RealPathUtil;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.exemplo.videoapp.R;

public class SelectVideo extends SherlockActivity {
	
	private final int TIME_LIMIT = 15;
	private static final int SIZE_LIMIT = 5*1048*1048; //5MB Limit
	
	private final int RECORDED_VIDEO = 0;
	
	private final int VIDEO_FROM_GALLERY = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("Select your video");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_uploadvideo);		
				
	}
		
	public void recordVideo(View v) {
		//Custom video capture:
		//startActivity(new Intent(this, VideoCapture.class));
		
		//Android's standard camera activiy:
		startActivityForResult(new Intent(MediaStore.ACTION_VIDEO_CAPTURE)
			.putExtra(android.provider.MediaStore.EXTRA_DURATION_LIMIT, TIME_LIMIT)
			.putExtra(android.provider.MediaStore.EXTRA_SIZE_LIMIT, SIZE_LIMIT+"L")
			.putExtra(android.provider.MediaStore.EXTRA_VIDEO_QUALITY, 1), RECORDED_VIDEO);
	}
	
	public void pickFromGallery(View v) {
		startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("video/*"), 
				VIDEO_FROM_GALLERY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode != RESULT_OK || data.getData() == null || data == null) {
			Toast.makeText(getApplicationContext(), "No video selected", Toast.LENGTH_LONG).show();
			return;
		}
		
		Log.i("UploadVideo", "Got video. Uri: " + data.getData());
				
		switch(requestCode) {
		case RECORDED_VIDEO:
			startActivity(new Intent(SelectVideo.this, Upload.class)
					.putExtra("path", getRealPath(data.getData())));
			break;
			
		case VIDEO_FROM_GALLERY:
			startActivity(new Intent(SelectVideo.this, Upload.class)
					.putExtra("path", getRealPath(data.getData())));
			break;
		}
	}
	
	/**
	 * Gets the real path for the file from the Uri.
	 * 
	 * @param uri File's Uri
	 * @return String with the path
	 */
	private String getRealPath(Uri uri) {
		String path = null;
				
		if(Build.VERSION.SDK_INT < 11) {
			path = RealPathUtil.getRealPathFromURI_BelowAPI11(SelectVideo.this, uri);
		} else if(Build.VERSION.SDK_INT < 19) {
			path = RealPathUtil.getRealPathFromURI_API11to18(SelectVideo.this, uri);
		} else {
			try {
				path = RealPathUtil.getRealPathFromURI_API19(SelectVideo.this, uri);				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				Log.e("SelectVideo", "Error getting the real path from Uri. Message: " + e.getMessage());
				Toast.makeText(getApplicationContext(), "Error getting file's path", Toast.LENGTH_SHORT).show();
			}
		}
		
		Log.i("SelectVideo", "Got Real Path: " + path);
		
		return path;
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
	
}