public class AuthorISBN 
{
	private int authorID;
	private String isbn;
	
	public AuthorISBN(int authorID, String isbn)
	{
		this.authorID = authorID;
		this.isbn = isbn;
	}

	public int getAuthorID() {
		return authorID;
	}

	public void setAuthorID(int newAuthorID) {
		authorID = newAuthorID;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String newIsbn) {
		isbn = newIsbn;
	}
	
	
}
