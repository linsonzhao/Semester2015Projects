package video.app.com.tabs;

import java.io.IOException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import video.app.com.ActionsList;
import video.app.com.adapter.GridAdapter;
import video.app.com.objects.Video;
import video.app.com.util.InternetUtil;
import video.app.com.util.Util;
import video.app.com.util.XMLParser;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.exemplo.videoapp.R;

/**
 * This class creates and show a GridView in a cloud format of all the videos.
 * @author victor
 *
 */
public class AllVideosFragment extends SherlockFragment implements OnItemClickListener, OnMenuItemClickListener{
	
	private final String VIDEOS_ENDPOINT = "videos?apikey=9061";
	//private final String VIDEOS_ENDPOINT = "videoslist.xml?apikey=9061";
	
	private InternetUtil iUtil;
	
	private Util util;
	
	private XMLParser XmlParser;
	
	private GridView list;
	
	private ProgressBar bar;
	
	private View v;
	
	private List<Video> videos;
	
	private TextView tryAgain;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		
		v = inflater.inflate(R.layout.activity_allvideosfragment, container, false);
		
		init();
		
		new Task().execute();
		
		return v;
	}
	
	private void init() {
		bar = (ProgressBar) v.findViewById(R.id.pBarAll);
		list = (GridView) v.findViewById(R.id.gvAnimalClouds);
		iUtil = new InternetUtil();
		XmlParser = new XMLParser();
		util = new Util(getSherlockActivity());

		list.setOnItemClickListener(this);
		
		tryAgain = (TextView) v.findViewById(R.id.tvTryAgain);
		tryAgain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new Task().execute();
			}
			
		});
	}
	
	private class Task extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			try {
				return iUtil.getResponse(util.getRestURL() + VIDEOS_ENDPOINT);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result!=null) {
				try {
					videos = XmlParser.getAllVideos(result);
					list.setAdapter(new GridAdapter(getSherlockActivity(), XmlParser.getAllVideos(result)));
					list.setVisibility(View.VISIBLE);
					bar.setVisibility(View.GONE);
					tryAgain.setVisibility(View.GONE);
				} catch (XmlPullParserException e) {
					e.printStackTrace();
					bar.setVisibility(View.GONE);
					tryAgain.setVisibility(View.VISIBLE);
					Toast.makeText(getSherlockActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					e.printStackTrace();
					bar.setVisibility(View.GONE);
					tryAgain.setVisibility(View.VISIBLE);
					Toast.makeText(getSherlockActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
				}
			} else {
				try {
					bar.setVisibility(View.GONE);
					tryAgain.setVisibility(View.VISIBLE);
					Toast.makeText(getSherlockActivity(), "Something went wrong", Toast.LENGTH_LONG).show();					
				} catch (Exception e) { e.printStackTrace(); }
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			list.setVisibility(View.GONE);
			tryAgain.setVisibility(View.GONE);
			bar.setVisibility(View.VISIBLE);
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		startActivity(new Intent(getSherlockActivity(), ActionsList.class)
				.putExtra("Video", videos.get(arg2)));
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		menu.add("Refresh")
			.setTitle("Refresh")
			.setIcon(R.drawable.ic_action_refresh)
			.setOnMenuItemClickListener(this)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		list.setVisibility(View.GONE);
		tryAgain.setVisibility(View.GONE);
		bar.setVisibility(View.VISIBLE);

		new Task().execute();
		return true;
	}
	
}
