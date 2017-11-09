public class Author 
{
	private int authorID;
	private String firstName;
	private String lastName;
	
	public Author(int authorID, String firstName, String lastName)
	{
		this.authorID = authorID;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public int getAuthorID() {
		return authorID;
	}

	public void setAuthorID(int newAuthorID) {
		authorID = newAuthorID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String newFirstName) {
		firstName = newFirstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String newLastName) {
		lastName = newLastName;
	}
	
	
}
