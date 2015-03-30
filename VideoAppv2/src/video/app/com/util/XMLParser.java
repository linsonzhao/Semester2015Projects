package video.app.com.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import video.app.com.objects.Video;

/**
 * Collection of methods dedicated to parse the XML response from the server.
 * @author victor
 *
 */
public class XMLParser {
	
	/**
	 * Parses the server's XML response into an array list of Video objects
	 * 
	 * @param XML	The server's response in XML
	 * @return	List of all Videos parsed from that XML
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public List<Video> getAllVideos(String XML) throws XmlPullParserException, IOException {
		
		if(XML == null) throw new XmlPullParserException("No XML");
		if(XML.startsWith("<html>")) throw new XmlPullParserException("Not XML");
		
		List<Video> videos = new ArrayList<Video>();
		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(new StringReader(XML));
        
        int eventType = xpp.getEventType();
        Video currentVideo = new Video();
        
        while (eventType != XmlPullParser.END_DOCUMENT) {
        	String TAG;
        	switch(eventType) {
        	case XmlPullParser.START_TAG:
        		TAG = xpp.getName();
        		
        		if(TAG.equals("video")) {
        			currentVideo = new Video();
        		} else if (currentVideo != null){
        			if(TAG.equals("description")) {
        				currentVideo.setDescription(xpp.nextText());
        			} else if(TAG.equals("videoFile")) {
        				currentVideo.setVideoFile(xpp.nextText());
        			} else if(TAG.equals("target")) {
        				currentVideo.setName(xpp.nextText());
        			} else if(TAG.equals("size")) {
        				currentVideo.setSize(xpp.nextText());
        			} else if(TAG.equals("tracking")) {
        				currentVideo.setTracking(xpp.nextText().equals("true"));
        			} else if(TAG.equals("videoId")) {
        				currentVideo.setVideoId(xpp.nextText());
        			} //else if(TAG.equals("thumbnail")) {
        				//currentVideo.setThumbnailPath(xpp.nextText());
        			//}
        		}
        		break;
        		
        	case XmlPullParser.END_TAG:
        		TAG = xpp.getName();
        		
        		if(TAG.equals("video") && currentVideo != null)
        			videos.add(currentVideo);
        		break;
        	}
        	eventType = xpp.next();
        }
		
		return videos;
	}
	
	/**
	 * Gets the XML response from the server, and checks if the user was subscribed or not.
	 * 
	 * @param XML	Server's XML response
	 * @return	If the user was subscribed true/false
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public boolean isSubscribed(String XML) throws XmlPullParserException, IOException {
		if(XML == null) throw new XmlPullParserException("No XML");

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(new StringReader(XML));
        
        int eventType = xpp.getEventType();
        
        boolean isSubscribed = false;
        
        while (eventType != XmlPullParser.END_DOCUMENT) {
        	String TAG;
        	switch(eventType) {
        	case XmlPullParser.START_TAG:
        		TAG = xpp.getName();
        		
        		if(TAG.equals("isSubscribed")) {
        			isSubscribed = xpp.nextText().equals("true");
        		}
        		break;        		
        	}
        	eventType = xpp.next();
        }

		return isSubscribed;
	}

}
