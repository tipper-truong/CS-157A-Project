
public class Titles {

	private String isbn;
	private String title;
	private int editionNumber;
	private int year;
	private int publisherID;
	private float price;

	public Titles(String isbn, String title, int editionNumber, int year, int publisherID, float price) 
	{
		this.isbn = isbn;
		this.title = title;
		this.editionNumber = editionNumber;
		this.year = year;
		this.publisherID = publisherID;
		this.price = price;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String newIsbn) {
		isbn = newIsbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String newTitle) {
		title = newTitle;
	}

	public int getEditionNumber() {
		return editionNumber;
	}

	public void setEditionNumber(int newEditionNumber) {
		editionNumber = newEditionNumber;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int newYear) {
		year = newYear;
	}


	public int getPublisherID() {
		return publisherID;
	}

	public void setPublisherID(int newPublisherID) {
		publisherID = newPublisherID;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float newPrice) {
		price = newPrice;
	}
	
	
}
