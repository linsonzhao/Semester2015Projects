package video.app.com.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Video Object that carries all the needed information through the Activities.
 * @author victor
 *
 */
public class Video implements Parcelable {

	private String name, description, videoFile, size, videoId, thumbnailPath;
	
	private boolean tracking;

	public Video(String name, String description, String videoFile, String size,
			boolean tracking, String videoId, String thumbnailPath, String animalId) {
		super();
		this.name = name;
		this.description = description;
		this.videoFile = videoFile;
		this.size = size;
		this.tracking = tracking;
		this.videoId = videoId;
		this.thumbnailPath = thumbnailPath;
	}
	
	public Video() {
		super();
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public boolean isTracking() {
		return tracking;
	}

	public void setTracking(boolean tracking) {
		this.tracking = tracking;
	}

	public String getVideoFile() {
		return videoFile;
	}

	public void setVideoFile(String videoFile) {
		this.videoFile = videoFile;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeArray(new Object[] {name, description, videoFile, size, tracking, videoId});
	}
	
	public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {

		@Override
		public Video createFromParcel(Parcel source) {
			return new Video(source);
		}

		@Override
		public Video[] newArray(int size) {
			return new Video[size];
		}
		
	};
	
	private Video(Parcel in) {
		Object[] o = in.readArray(null);
		name = (String) o[0];
		description = (String) o[1];
		videoFile = (String) o[2];
		size = (String) o[3];
		tracking = (Boolean) o[4];
		videoId = (String) o[5];
	}
}
