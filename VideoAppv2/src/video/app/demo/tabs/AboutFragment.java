package video.app.demo.tabs;

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
 * Class with all the information related to the about fragment
 * @author victor
 *
 */
public class AboutFragment extends SherlockFragment implements OnClickListener {

	private View v;
	
	private LinearLayout about, authors;
	
	//List of authors
	private final String[] autores = {"Dr. Changseok Bae", "Dr. Vera Chung", "Zhenghao Chen", "Chongli Zhao", 
			"Ximeng Zhao", "Jun Shou", "Feng Sha", "Guang Liu", "Victor Santiago"};
	
	//About text
	private final String sobre = "The aim of this project is to enhance the visiting experience of visitors " +
						"by utilizing established sensor networks and visitors' mobile devices, and " +
						"to provide attractive services to on-site visitors and online visitors.\n\n" +
						"To demonstrate the concept, in this project we are going to build a " +
						"\"digital zoo\" which will bring on-site visitors novel and unique experience " +
						"based on AR  technology, meanwhile provide attractive remote visiting " +
						"services to online visitors.";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		v = inflater.inflate(R.layout.tab_about, container, false);
		
		init();
		
		return v;
	}
	
	private void init() {
		about = (LinearLayout) v.findViewById(R.id.llAbout);
		about.setOnClickListener(this);
		
		authors = (LinearLayout) v.findViewById(R.id.llAuthors);
		authors.setOnClickListener(this);
	}
	
	private void popDialog(String title, String message, boolean cancelable, 
			DialogInterface.OnClickListener okButton, ArrayAdapter<String> adapter) {
		AlertDialog.Builder adBuilder = new AlertDialog.Builder(getSherlockActivity());
		if(adapter == null)
			adBuilder.setMessage(message);
		else
			adBuilder.setAdapter(adapter, null);
		adBuilder.setTitle(title);
		adBuilder.setCancelable(cancelable);
		adBuilder.setPositiveButton(android.R.string.ok, okButton);
		AlertDialog adFinal = adBuilder.create();
		adFinal.show();
	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()) {
			case R.id.llAbout:
				popDialog("About", sobre, true, 
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}, null);
				break;
				
			case R.id.llAuthors:
				popDialog("Authors", null, true, 
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}, new ArrayAdapter<String>(getSherlockActivity(), 
						android.R.layout.select_dialog_item, autores));
				break;
		}
	}

}
