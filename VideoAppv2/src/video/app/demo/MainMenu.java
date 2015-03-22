package video.app.demo;

import video.app.demo.adapter.TabsAdapter;
import video.app.demo.tabs.AboutFragment;
import video.app.demo.tabs.AllVideosFragment;
import video.app.demo.util.RealPathUtil;
import video.app.demo.util.Util;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.exemplo.videoapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Main menu from the application.
 * This class only handles the tabs. To see and edit the content of each tab,
 * go to the Tabs package.
 * 
 * @author victor
 * @see AboutFragment, AllVideosFragment
 */
public class MainMenu extends SherlockFragmentActivity implements OnMenuItemClickListener {

	private static final int CONFIGURATIONS = 0;
	private static final int UPLOAD_VIDEO = 1;
	
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	
	Util util;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
				
		util = new Util(this);
		
		mViewPager = new ViewPager(this);
		mViewPager.setId(1);
		
		setContentView(mViewPager);
		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mTabsAdapter = new TabsAdapter(this, mViewPager);

		Tab animalsList = bar.newTab();
		animalsList.setText("Animals");
		
		Tab about = bar.newTab();
		about.setText("About");
		
		
		mTabsAdapter.addTab(
				animalsList,
				AllVideosFragment.class, null);
		
		mTabsAdapter.addTab(
				about,
				AboutFragment.class, null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(0,0,0,"Configurations")
			.setTitle("Configurations")
			.setOnMenuItemClickListener(this)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		
		menu.add(0,1,1,"Upload video")
			.setTitle("Upload Video")
			.setIcon(R.drawable.ic_action_upload)
			.setOnMenuItemClickListener(this)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
						
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch(item.getItemId()) {
		case CONFIGURATIONS:
			popDialog();
			break;
			
		case UPLOAD_VIDEO:
			startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("video/*"), 
					0);
			//startActivity(new Intent(this, SelectVideo.class));
			break;
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode != RESULT_OK || data.getData() == null || data == null) {
			Toast.makeText(getApplicationContext(), "No video selected", Toast.LENGTH_LONG).show();
			return;
		}
		
		startActivity(new Intent(MainMenu.this, Upload.class)
			.putExtra("path", getRealPath(data.getData())));
		
	}

	private void popDialog() {
		LayoutInflater lInflater = MainMenu.this.getLayoutInflater();
		
		final View v = lInflater.inflate(R.layout.dialog_configip, null);
		
		final EditText ipText = (EditText) v.findViewById(R.id.etIP);
		ipText.setText(util.getIP());
		
		AlertDialog.Builder adBuilder = new AlertDialog.Builder(MainMenu.this);
		adBuilder.setTitle("Configurations");
		adBuilder.setView(v);
		adBuilder.setCancelable(true);
		adBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if(!ipText.getText().toString().isEmpty())
					util.setIP(ipText.getText().toString());
			}
		});
		AlertDialog adFinal = adBuilder.create();
		adFinal.show();
	}
	
	private boolean verifyGooglePlayServices() {
		int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainMenu.this);
		
		if (isAvailable != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, 
					MainMenu.this, 10);
				dialog.show();
			} else {
				Log.i("Menu", "This device is not supported.");
	            finish();
			}
			return false;
		}
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		verifyGooglePlayServices();
	}
	
	private String getRealPath(Uri uri) {
		String path = null;
				
		if(Build.VERSION.SDK_INT < 11) {
			path = RealPathUtil.getRealPathFromURI_BelowAPI11(MainMenu.this, uri);
		} else if(Build.VERSION.SDK_INT < 19) {
			path = RealPathUtil.getRealPathFromURI_API11to18(MainMenu.this, uri);
		} else {
			try {
				path = RealPathUtil.getRealPathFromURI_API19(MainMenu.this, uri);				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				Log.e("SelectVideo", "Error getting the real path from Uri. Message: " + e.getMessage());
				Toast.makeText(getApplicationContext(), "Error getting file's path", Toast.LENGTH_SHORT).show();
			}
		}
		
		Log.i("SelectVideo", "Got Real Path: " + path);
		
		return path;
	}
	
}