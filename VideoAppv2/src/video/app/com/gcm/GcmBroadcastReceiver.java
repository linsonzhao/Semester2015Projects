package video.app.com.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Receiver for the Google Cloud Message
 * 
 * @author victor
 *
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		ComponentName comp = new ComponentName(arg0.getPackageName(),
				GcmIntentService.class.getName());
		startWakefulService(arg0, (arg1.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}

}
