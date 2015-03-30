package video.app.com.adapter;

import com.exemplo.videoapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Simple adapter that receives a String[] and shows a list.
 * It's used on ActionsList activity to display the options.
 * @author victor
 *
 */
public class SimpleListAdapter extends BaseAdapter {
	
	private String[] list;
	
	private Context c;
	
	public SimpleListAdapter(Context c, String[] s) {
		list = s;
		this.c = c;
	}

	@Override
	public int getCount() {
		return list.length;
	}

	@Override
	public Object getItem(int arg0) {
		return list[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View v = arg1;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			v = inflater.inflate(R.layout.row_simplelist, arg2, false);
		}
		
		final TextView text = (TextView) v.findViewById(R.id.tvListItem);
		text.setText(list[arg0]);
		
		return v;
	}

}
