
public class Publisher {
	
	private int publisherID;
	private String publisherName;
	
	public Publisher(int publisherID, String publisherName)
	{
		this.publisherID = publisherID;
		this.publisherName = publisherName;
	}

	public int getPublisherID() {
		return publisherID;
	}

	public void setPublisherID(int newPublisherID) {
		publisherID = newPublisherID;
	}

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String newPublisherName) {
		publisherName = newPublisherName;
	}
	
	
}
