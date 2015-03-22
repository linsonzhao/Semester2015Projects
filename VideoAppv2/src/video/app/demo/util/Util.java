package video.app.demo.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Collection of useful methods to access and retrieve information stored on the
 * SharedPreferences, such as the custom server's IP.
 * 
 * @author victor
 *
 */
public class Util {
	
	private final String FIXED_IP = "192.168.43.153:8080";
	private final String REST_URL = "/videotracking/rest/";
	private final String URL = "/videotracking";
	private SharedPreferences s;
	
	public Util(Context c) {
		s = c.getSharedPreferences("VAPreferences", c.MODE_PRIVATE);
	}
	
	public String getIP() {
		if(s.getString("IP", null) != null)
			return s.getString("IP", null);
		return FIXED_IP;
	}
		
	public void setIP(String IP) {
		s.edit().putString("IP", IP).commit();
	}
	
	public String getRestURL() {
		return "http://" + getIP() + REST_URL;
	}

	public String getURL() {
		return "http://" + getIP() + URL;
	}

}
