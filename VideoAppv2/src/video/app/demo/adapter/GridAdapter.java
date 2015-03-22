package video.app.demo.adapter;

import java.util.List;

import video.app.demo.objects.Video;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.exemplo.videoapp.R;

/**
 * This adapter handles the creation of the grid clouds on the MainMenu
 * @author victor
 *
 */
public class GridAdapter extends BaseAdapter {
	
	private List<Video> videos;
	
	private Context mContext;
	
	public GridAdapter(Context c, List<Video> videos) {
		this.videos = videos;
		mContext = c;
	}

	@Override
	public int getCount() {
		return videos.size();
	}

	@Override
	public Object getItem(int arg0) {
		return videos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View v = arg1;
		
		if(v == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			v = inflater.inflate(R.layout.row_cloud, arg2, false);
		}
		
		final TextView target = (TextView) v.findViewById(R.id.tvVideoTarget);
		target.setText(videos.get(arg0).getName());
		
		return v;
	}

}
