package video.app.com;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.actionbarsherlock.app.SherlockActivity;
import com.exemplo.videoapp.R;

/**
 * Introduction activity.
 * The first page the user will see, for about 2 seconds, and then
 * will be redirected to the MainMenu activity.
 * 
 * @author victor
 *
 */
public class Introduction extends SherlockActivity {
	
	private final int TIME = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_introduction);
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					startActivity(new Intent(Introduction.this, MainMenu.class));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}, TIME);
	}
		
}
