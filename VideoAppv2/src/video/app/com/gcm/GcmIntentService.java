package video.app.com.gcm;

import video.app.com.PlayVideo;
import video.app.com.objects.Video;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.exemplo.videoapp.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This class handles what to do with the data received. In this case, we launch
 * a notification. See method sendNotification()
 * 
 * @author victor
 *
 */
public class GcmIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		Bundle extras = arg0.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(arg0);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString(), null, null);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification(
						"Deleted messages on server: " + extras.toString(),
						null, null);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) { // Status: Success
				Log.i("GCMIntentService",
						"Completed work @ " + SystemClock.elapsedRealtime());
				sendNotification(extras.getString("message"),
						extras.getString("link"), extras.getString("title"));
				Log.i("GCMIntentService", "Received: " + extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(arg0);
	}

	/**
	 * Generates and shows a notification on the user Android device. The
	 * notification shows a title, a message and the default launcher icon. When
	 * the user clicks on the notification, he's going to be redirected to the
	 * PlayVideo activity to see the video from the link.
	 * 
	 * @param msg
	 *            The message shown on the notification.
	 * @param link
	 *            The video link which the user will be redirected to.
	 * @param title
	 *            Notification's title
	 */
	private void sendNotification(String msg, String link, String title) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, PlayVideo.class).putExtra("Video",
						generateVideo(msg, link)),
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title).setAutoCancel(true).setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

	/**
	 * Generates a Video object with all the information needed to be passed to
	 * the PlayVideo activity
	 * 
	 * @param name
	 *            Here, the name will be the message from the notification,
	 *            which will be shown on the ActionBar from the PlayVideo
	 *            activity.
	 * @param videoFile
	 *            The link for the video
	 * @return A video object containing the information to be passed to the
	 *         PlayVideo activity.
	 * @see Video
	 */
	private Video generateVideo(String name, String videoFile) {
		Video v = new Video();
		v.setName(name);
		v.setVideoFile(videoFile);
		return v;
	}

}
