package video.app.com.tabs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.exemplo.videoapp.R;

/**
 *
 */
public class DetectFragment extends SherlockFragment{

	private View v;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		v = inflater.inflate(R.layout.tab_detect, container, false);
		
		return v;
	}

}
