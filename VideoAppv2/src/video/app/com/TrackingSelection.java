package video.app.com;

import java.io.IOException;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParserException;

import video.app.com.objects.Video;
import video.app.com.util.InternetUtil;
import video.app.com.util.Util;
import video.app.com.util.XMLParser;
import video.app.com.views.ScallingImageView;
import video.app.com.views.SelectorView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.exemplo.videoapp.R;

/**
 * This activity is responsible to show the first frame of a video
 * to the user, and when the user touches the image, a red square appears,
 * and a track button sends this coordinates to a server.
 * 
 * @author victor
 *
 */
public class TrackingSelection extends SherlockActivity implements OnMenuItemClickListener {
	private final int x = 0;
	private final int y = 1;
	private final int w = 2;
	private final int h = 3;

	private SelectorView sView;
	private SeekBar sBar;
	private InternetUtil iUtil;
	private Util util;
	private XMLParser xParser;
	private ScallingImageView background;
	private ProgressBar pBar;
	private LinearLayout drawingPad;
	private RelativeLayout container;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("Select tracking object");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.activity_trackingselection);
		
		init();
	}
	
	/**
	 * Initiates the basic variables
	 */
	private void init() {
		pBar = (ProgressBar) findViewById(R.id.pBarTracking);
		drawingPad = (LinearLayout) findViewById(R.id.view_drawing_pad);
		background = (ScallingImageView) findViewById(R.id.videoToTrack);
		sBar = (SeekBar) findViewById(R.id.seekBar1);
		container = (RelativeLayout) findViewById(R.id.layout_container);

		iUtil = new InternetUtil();
		util = new Util(TrackingSelection.this);
		xParser = new XMLParser();
		
		new LoadFirstFrame().execute();
		
		Log.i("Tracking", "Trying to track : " + util.getURL() + getVideo().getVideoFile());		
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
		
	/**
	 * Gets the square coordinates and size on the phone, and calculates the
	 * equivalent for the actual video size.
	 * 
	 * @return An array of ints with the X and Y position from the top left
	 * corner, proportional to the real video size, and the Width and Height of
	 * the square, respectively.
	 */
	public int[] getPosition() {
		//ActualWidthSize = (ActualVideoSize * DeviceWidthSize) / DeviceSize
		int phoneSizeWidth = background.getMeasuredWidth();
		int phoneSizeHeight = background.getMeasuredHeight();
		int realSizeWidth = background.getDrawable().getIntrinsicWidth();
		int realSizeHeight = background.getDrawable().getIntrinsicHeight();
						
		int result[] = new int[4];
		
		if(realSizeWidth == 0 || realSizeHeight == 0 || 
				phoneSizeWidth == 0 || phoneSizeHeight == 0)
			throw new NullPointerException("No real size found!");
		
		Log.i("Tracking", "Got sizes. Phone size width: " + phoneSizeWidth + ", " +
				"Phone size height: " + phoneSizeHeight + ", Actual video width: " + realSizeWidth + 
				", Video height: " + realSizeHeight);
		
		Log.i("Tracking", "Positions: X = " + sView.getPositionX() + " / Y = " + sView.getPositionY() + 
				" / W = " + sView.getSquareWidth() + "/ H = " + sView.getSquareHeight());
				
		result[x] = (realSizeWidth * sView.getPositionX()) / phoneSizeWidth;
		result[y] = (realSizeHeight * sView.getPositionY()) / phoneSizeHeight;
		result[w] = (realSizeWidth * sView.getSquareWidth()) / phoneSizeWidth;
		result[h] = (realSizeHeight * sView.getSquareHeight()) / phoneSizeHeight;
		
		Log.i("Tracking", "Got coordinates: X = " + result[x] + " / Y = " + result[y] + 
				" / Width = " + result[w] + " / Height = " + result[h]);
		
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Track It!")
			.setOnMenuItemClickListener(this)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		if(sView.hasTouched())
			new SendTrackInfo().execute();
		else
			Toast.makeText(getApplicationContext(), "Select a tracking object first", 
					Toast.LENGTH_SHORT).show();
		return true;
	}
	
	public Video getVideo() {
		return (Video) getIntent().getExtras().getParcelable("Video");
	}
	
	private class SendTrackInfo extends AsyncTask<Void, Void, String> {

		ProgressDialog pDialog;
		
		@Override
		protected String doInBackground(Void... arg0) {
			try {
				int[] positions = getPosition();
				Log.i("Coordinates", "Sending: X = " + positions[x] + " / Y = " + positions[y] + 
						" Height = " + positions[h] + " Width = " + positions[w] + 
						" / To: " + util.getRestURL() + "videos/coordinates" + " / Source file: " + 
						getVideo().getVideoFile());
				return iUtil.getResponse(util.getRestURL() + "videos/coordinates?apikey=9061&videoid=" 
						+ getVideo().getVideoId() + "&sourcefile=" + getVideo().getVideoFile()+ 
						"&x=" + positions[x] + "&y=" + positions[y] + "&w=" + positions[w] + 
						"&h=" + positions[h]);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(pDialog.isShowing())
				pDialog.dismiss();
			
			if(result != null) {
				try {
					startActivity(new Intent(TrackingSelection.this, PlayVideo.class)
							.putExtra("Video", xParser.getAllVideos(result).get(0)));
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else Toast.makeText(TrackingSelection.this, "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
			
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = ProgressDialog.show(TrackingSelection.this, "Wait", "Tracking video...", true);
		}
		
	}
	
	private class LoadFirstFrame extends AsyncTask<Void, Void, Bitmap> {

		@SuppressLint("NewApi")
		@Override
		protected Bitmap doInBackground(Void... arg0) {
			final String trackingURL = util.getURL() + getVideo().getVideoFile();
			//final String trackingURL = "http://192.168.0.16/project/raw_video/baby_kangaroo.mp4";

			MediaMetadataRetriever m = new MediaMetadataRetriever();
			
			try {
								
				if(Build.VERSION.SDK_INT >= 14)
					m.setDataSource(trackingURL, 
							new HashMap<String,String>());
				else
					m.setDataSource(trackingURL);
				return m.getFrameAtTime(1, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Tracking", e.getMessage());
				return null;
			} finally {
				m.release();
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			
			if(result == null) {
				Toast.makeText(getApplicationContext(), "Can't load video.", 
						Toast.LENGTH_SHORT).show();
				finish();
			} else {
				background.setImageBitmap(result);
				
				pBar.setVisibility(View.GONE);
				container.setVisibility(View.VISIBLE);
								
				sView = new SelectorView(TrackingSelection.this);		
				sView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
						LayoutParams.MATCH_PARENT, 10));
				drawingPad.addView(sView);
				
				sBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					int progressChanged = 0;

					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						progressChanged = progress;
						sView.updateSquareSize(progressChanged);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						sView.updateSquareSize(progressChanged);
					}
					
				});
				
				Toast.makeText(getApplicationContext(), "Use the bar on the bottom to adjust the square size", 
						Toast.LENGTH_LONG).show();
			}
			
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pBar.setVisibility(View.VISIBLE);
			container.setVisibility(View.GONE);
			
			Toast.makeText(getApplicationContext(), "Loading tracking image...", 
					Toast.LENGTH_SHORT).show();
		}
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}	
}