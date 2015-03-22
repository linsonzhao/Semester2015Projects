package video.app.demo;

import java.io.File;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import video.app.demo.util.InternetUtil;
import video.app.demo.util.Util;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.exemplo.videoapp.R;

/**
 * Handles the upload from the user's device to the server
 * @author victor
 *
 */
public class Upload extends SherlockActivity {
	
	private EditText target, description;
	
	private InternetUtil iUtil;
	
	private Util u;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("Upload it!");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_upload);
		
		init();
		
	}
	
	private void init() {
		iUtil = new InternetUtil();
		u = new Util(Upload.this);
		
		target = (EditText) findViewById(R.id.etAnimalName);
		description = (EditText) findViewById(R.id.etDescription);
	}
	
	public void enviar(View v) {
		if(!target.getText().toString().isEmpty() ||
				!description.getText().toString().isEmpty())
			new UploadFile().execute(new String[] {u.getURL() + "/uploadfile",	getPath(), 
					target.getText().toString(), description.getText().toString()});
		else
			Toast.makeText(getApplicationContext(), "Please, complete all fields above", 
					Toast.LENGTH_SHORT).show();
	}
	
	private String getPath() {
		return getIntent().getExtras().getString("path");
	}
	
	private class UploadFile extends AsyncTask<String, Void, String> {

		ProgressDialog pDialog;
		
		@Override
		protected String doInBackground(String... arg0) {
			try {
				Log.i("Upload", "Trying to upload video... Target: " + arg0[2] + " / Description: " + arg0[3]);
				Log.i("Upload", "Sending request to: " + arg0[0]);
				
				MultipartEntityBuilder entity = MultipartEntityBuilder.create();
				entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				entity.addPart("target", new StringBody(arg0[2], ContentType.MULTIPART_FORM_DATA));
				entity.addPart("description", new StringBody(arg0[3], ContentType.MULTIPART_FORM_DATA));
				entity.addPart("file", new FileBody(new File(arg0[1])));
				return iUtil.postVideo(arg0[0], entity.build());				
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Upload", "Upload error. Message: " + e.getMessage());
				return "Error: " + e.getMessage();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if(pDialog.isShowing())
				pDialog.dismiss();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(pDialog.isShowing())
				pDialog.dismiss();
			
			if(result.contains("Error report")) {
				//Toast.makeText(getApplicationContext(), result.replace("Error: ", ""), Toast.LENGTH_LONG).show();
				Toast.makeText(getApplicationContext(), "Error. Try again later.", Toast.LENGTH_LONG).show();
			}
			else {
				Toast.makeText(getApplicationContext(), "Upload completed", Toast.LENGTH_SHORT).show();
				Log.i("Upload", "Response: " + result);
			}
			
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = ProgressDialog.show(Upload.this, "Wait", "Uploading video", true);
			pDialog.show();
		}
		
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;			
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
