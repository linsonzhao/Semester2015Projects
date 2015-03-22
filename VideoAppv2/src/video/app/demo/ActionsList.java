package video.app.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
import video.app.demo.adapter.SimpleListAdapter;
import video.app.demo.objects.Video;
import video.app.demo.util.InternetUtil;
import video.app.demo.util.Util;
import video.app.demo.util.XMLParser;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.exemplo.videoapp.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This class handles the creation of the list the user sees when he selects an
 * animal cloud. This class handles the subscriptions as well.
 * 
 * @author victor
 *
 */
public class ActionsList extends SherlockActivity implements
		OnItemClickListener {

	private List<String> OPTIONS;
	private final String PROJECT_ID = "227461746139";
	private InternetUtil iUtil;
	private Util u;
	private ListView l;
	private ProgressBar bar;
	private GoogleCloudMessaging gcm;
	private boolean isSubscribed;
	private XMLParser xml;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_actionslist);

		init();

		new VerifySubscription().execute();

	}

	private void init() {
		l = (ListView) findViewById(R.id.lvActions);
		iUtil = new InternetUtil();
		u = new Util(ActionsList.this);
		bar = (ProgressBar) findViewById(R.id.pbAction);
		OPTIONS = new ArrayList<String>();

		OPTIONS.add("Subscribe");
		if (!getVideo().isTracking()) {
			OPTIONS.add("Watch video");
			OPTIONS.add("Track animal");
		} else
			OPTIONS.add("Watch tracking video");

		xml = new XMLParser();

		gcm = GoogleCloudMessaging.getInstance(this);
	}

	private class VerifySubscription extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			try {
				return iUtil.getResponse(u.getURL() + "/issubscribed?&videoid="
						+ getVideo().getName() + "&userid=" + getDeviceID());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				isSubscribed = xml.isSubscribed(result);
				if (isSubscribed) {
					OPTIONS.set(0, "Unsubscribe");
				}
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bar.setVisibility(View.GONE);
			l.setVisibility(View.VISIBLE);
			l.setAdapter(new SimpleListAdapter(ActionsList.this, OPTIONS
					.toArray(new String[OPTIONS.size()])));
			l.setOnItemClickListener(ActionsList.this);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			bar.setVisibility(View.VISIBLE);
			l.setVisibility(View.GONE);
		}

	}

	private class Subscribe extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			bar.setVisibility(View.VISIBLE);
			l.setVisibility(View.GONE);
		}

		@Override
		protected String doInBackground(Void... arg0) {
			try {
				return iUtil.getResponse(subscribe(
						shouldSubscribe(isSubscribed), getDeviceID(),
						getVideo().getName()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null)
				if (OPTIONS.get(0).equals("Subscribe"))
					OPTIONS.set(0, "Unsubscribe");
				else
					OPTIONS.set(0, "Subscribe");
			bar.setVisibility(View.GONE);
			l.setVisibility(View.VISIBLE);
			l.setAdapter(new SimpleListAdapter(ActionsList.this, OPTIONS
					.toArray(new String[OPTIONS.size()])));
		}

	}

	private Video getVideo() {
		return (Video) getIntent().getExtras().getParcelable("Video");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg2) {
		case 0:
			new Subscribe().execute();
			break;

		case 1:
			startActivity(new Intent(ActionsList.this, PlayVideo.class)
					.putExtra("Video", getVideo()));
			break;

		case 2:
			startActivity(new Intent(ActionsList.this, TrackingSelection.class)
					.putExtra("Video", getVideo()).putExtra("isLocal", false));
			break;
		}
	}

	private String getDeviceID() {
		String id = "";

		try {
			id = gcm.register(PROJECT_ID);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return id;
	}

	private String subscribe(boolean subscribe, String userid, String videoid) {
		return u.getURL() + "/subscribe?&subscribe=" + subscribe + "&userid="
				+ userid + "&videoid=" + videoid;
	}

	private boolean shouldSubscribe(boolean b) {
		if (isSubscribed)
			isSubscribed = false;
		else
			isSubscribed = true;
		return isSubscribed;
	}
}
