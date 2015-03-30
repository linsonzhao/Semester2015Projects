package video.app.com;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import video.app.com.util.RealPathUtil;
import video.app.com.views.CameraPreview;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

/**
 * Custom video capture class to be used on the future, if decide to capture
 * videos from the users device and upload to the server with custom and pre-determined
 * quality, format and length.
 * @author victor
 *
 */
public class VideoCapture extends SherlockActivity implements OnMenuItemClickListener {

	private Camera mCamera;
	private CameraPreview mPreview;
	private MediaRecorder mMediaRecorder;
	private static final String TAG = "VideoCapture";
	public static final int MEDIA_TYPE_VIDEO = 2;
	private boolean isRecording = false;
	private static final long FILESIZE_LIMIT = 10983040L; //10MB
	private static final int TIME_LIMIT = 15000; //15s
	private static String fname;
	private boolean onInfoCalled = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getSupportActionBar().setTitle("Record your video");
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().hide();
		
		init();
		
	}
	
	private void init() {
		Toast.makeText(getApplicationContext(), "Tap the screen to record", 
				Toast.LENGTH_LONG).show();
		
		mCamera = getCameraInstance();
		
		mPreview = new CameraPreview(this, mCamera);
		setContentView(mPreview);
		mPreview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				record();
			}
			
		});
		
		if(checkCameraHardware(this))
			Log.i(TAG, "This device has a camera.");
		else Log.i(TAG, "This device doesn't have a camera.");
		
	}
	
	private void record() {
		if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            Toast.makeText(getApplicationContext(), "Recording stopped", Toast.LENGTH_SHORT).show();
            isRecording = false;
            
            Uri videoUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
            
            //Scan file so it appears on Gallery:
    		sendBroadcast(new Intent(
    	    	    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
    	    	    Uri.parse("file://"+Environment.getExternalStoragePublicDirectory(
    	  	              Environment.DIRECTORY_MOVIES)+"/ZooApp/")));
    		
            //Start Upload activity:
            startActivity(new Intent(this, Upload.class)
            		.putExtra("path", getRealPath(videoUri)));
        } else {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                // inform the user that recording has started
                Toast.makeText(getApplicationContext(), "Recording", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Tap again to stop recording", 
                		Toast.LENGTH_LONG).show();
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }
	}
	
	private boolean prepareVideoRecorder(){

	    //mCamera = getCameraInstance();
	    mMediaRecorder = new MediaRecorder();
	    
	    if(mCamera == null) 
			throw new NullPointerException("Cannot get camera, or camera doesn't exist.");
	    
	    // Step 1: Unlock and set camera to MediaRecorder
	    mCamera.unlock();
	    mMediaRecorder.setCamera(mCamera);
	    

	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	    //mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	    
	    //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
	    //mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

	    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

	    mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

	    mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
	    
	    mMediaRecorder.setMaxFileSize(FILESIZE_LIMIT);
	    
	    mMediaRecorder.setMaxDuration(TIME_LIMIT);
	    
	    //mMediaRecorder.setVideoSize(480, 360);
	    
	    mMediaRecorder.setOnInfoListener(new OnInfoListener() {

			@Override
			public void onInfo(MediaRecorder mr, int what, int extra) {
				if(onInfoCalled)
					return;
				
				if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
					Toast.makeText(getApplicationContext(), "Video time limit reached!", 
							Toast.LENGTH_LONG).show();
					record();
					onInfoCalled = true;
				} else if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
					Toast.makeText(getApplicationContext(), "Video filesize limit reached!", 
							Toast.LENGTH_LONG).show();
					record();
					onInfoCalled = true;
				}
			}
	    	
	    });

	    // Step 10: Prepare configured MediaRecorder
	    try {
	        mMediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    return true;
	}
	
	private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }
	
	private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_MOVIES), "ZooApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    
	    if(type == MEDIA_TYPE_VIDEO) {
	    	fname = "VID_"+ timeStamp + ".mp4";
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	private Camera getCameraInstance() {
        Camera camera = null;
         
        try {
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Cannot get camera, or camera doesn't exist. Error message: " + 
            		e.getMessage());
        }
        
        return camera;
    }
	
	private String getRealPath(Uri uri) {
		String path = null;
				
		if(Build.VERSION.SDK_INT < 11) {
			path = RealPathUtil.getRealPathFromURI_BelowAPI11(VideoCapture.this, uri);
		} else if(Build.VERSION.SDK_INT < 19) {
			path = RealPathUtil.getRealPathFromURI_API11to18(VideoCapture.this, uri);
		} else {
			try {
				path = RealPathUtil.getRealPathFromURI_API19(VideoCapture.this, uri);				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				Log.e("VideoCapture", "Error getting the real path from Uri. Message: " + e.getMessage());
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add("Capture video")
			.setTitle("Capture video")
			.setIcon(android.R.drawable.ic_menu_camera)
			.setOnMenuItemClickListener(this)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		record();
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseMediaRecorder();
        releaseCamera();
        finish();
	}
	
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}

}
