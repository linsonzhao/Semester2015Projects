package video.app.com.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import android.util.Log;

/**
 * Useful methods to connect to facilitate the connection to the server.
 * @author victor
 *
 */
public class InternetUtil {
	
	private static final int DEFAULT_TIMEOUT = 30000;
			
	/**
	 * This method makes a simple GET Request to an specified URL.
	 * 
	 * @param URL	The URL to be connected to (with the GET parameters)
	 * @return		The server's response
	 * @throws Exception
	 */
	public String getResponse(String URL) throws Exception {
		final HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT);
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpGet httpGet = new HttpGet(URL);
		HttpResponse response = httpclient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		InputStream is = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		is.close();
		Log.i("RESPONSE", sb.toString());
		return sb.toString();
	}
	
	/**
	 * Simple method to do a POST request.
	 * 
	 * @param URL	The URL to do the POST request
	 * @param entityPost	The entity with the POST parameters
	 * @return		The server's response
	 * @throws Exception
	 */
	public String postVideo(String URL, HttpEntity entityPost) throws Exception {
		final HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT);
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpPost hPost = new HttpPost(URL);
		hPost.setEntity(entityPost);
		HttpResponse response = httpclient.execute(hPost);
		HttpEntity entity = response.getEntity();
		InputStream is = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		is.close();
		Log.i("RESPONSE", sb.toString());
		return sb.toString();
	}

}
