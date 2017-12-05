import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
/**
 * Database manipulation and operations for Titles, Author, Publisher, and Author ISBN
 * @author CS 157A Group #3
 */
public class Database {

	//JDBC Driver Name and Database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL_INITIAL =  "jdbc:mysql://localhost:3306/?autoReconnect=true&useSSL=false"; // use when Books database has not been created
	private static final String DB_URL =  "jdbc:mysql://localhost:3306/Books?autoReconnect=true&useSSL=false"; // use when Books database has been created

	// Database Name
	private final String DB_NAME = "Books";

	// Database Table Names;
	private final String TITLES_TABLE = "Titles";
	private final String AUTHOR_TABLE = "Author";
	private final String PUBLISHER_TABLE = "Publisher";
	private final String AUTHOR_ISBN_TABLE = "AuthorISBN";

	// Database Table Creation Queries
	private final String CREATE_TITLES_QUERY = "CREATE TABLE Titles " + 
			"("
			+ "isbn CHAR(10) not NULL, " 
			+ "title VARCHAR(500) not NULL, "
			+ "editionNumber INTEGER not NULL, "
			+ "year CHAR(4) not NULL, " 
			+ "publisherID INTEGER not NULL, "
			+ "price FLOAT not NULL, "
			+ "PRIMARY KEY ( isbn )"
			+ ")";

	private final String CREATE_AUTHOR_QUERY = "CREATE TABLE Author " + 
			"("
			+ "authorID INTEGER not NULL, "
			+ "firstName CHAR(20) not NULL, "
			+ "lastName CHAR(20) not NULL, "
			+ " PRIMARY KEY ( authorID )"
			+ ")";

	private final String CREATE_PUBLISHER_QUERY = "CREATE TABLE Publisher " + 
			"("
			+ "publisherID INTEGER not NULL, "
			+ "publisherName CHAR(100) not NULL"
			+ ")";

	private final String CREATE_AUTHOR_ISBN_QUERY = "CREATE TABLE AuthorISBN " + 
			"("
			+ "authorID INTEGER not NULL, "
			+ "isbn CHAR(10) not NULL"
			+ ")";

	// Database credentials
	private final String USERNAME = "root";
	private final String PASSWORD = "";

	private Connection dbConnect;
	private Statement stmt;

	/**
	 * Constructing the Books Database and it's required tables --> Titles, Author, Publisher, AuthorISBN
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Database() throws ClassNotFoundException, SQLException
	{
		dbConnect = null;
		stmt = null;

		// Connect to Database
		Class.forName(JDBC_DRIVER);
		System.out.println("Connecting to database...");
		dbConnect = DriverManager.getConnection(DB_URL_INITIAL, USERNAME, PASSWORD);
		boolean dbExist = checkDatabaseExists(DB_NAME);

		// Create Books Database
		createBooksDatabase(dbExist); 

		// Assuming Books Database has been created, create a new connection
		dbConnect = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

		// Create Tables Titles, Author, Publisher, AuthorISBN
		boolean tableExists = checkTableExists(TITLES_TABLE);
		createTable(tableExists, TITLES_TABLE, CREATE_TITLES_QUERY);

		tableExists = checkTableExists(AUTHOR_TABLE);
		createTable(tableExists, AUTHOR_TABLE, CREATE_AUTHOR_QUERY);

		tableExists = checkTableExists(PUBLISHER_TABLE);
		createTable(tableExists, PUBLISHER_TABLE, CREATE_PUBLISHER_QUERY);

		tableExists = checkTableExists(AUTHOR_ISBN_TABLE);
		createTable(tableExists, AUTHOR_ISBN_TABLE, CREATE_AUTHOR_ISBN_QUERY);
		
		// Parse 157a.sql file and insert 15 entries for each table in Books database
		File inputFile = new File("src/157a.sql");
		executeSqlScript(dbConnect, inputFile);
		
		selectPublishers(dbConnect);
		selectAuthors(dbConnect);
		selectSpecificPublisher(dbConnect, 7); // J.R.R. Tolkien
		System.out.println("\n");
		System.out.println("Before adding new author...");
		selectAuthors(dbConnect);
		System.out.println("\n");
		System.out.println("Add New Author");
		Author a = new Author(18, "John", "Doe");
		insertAuthor(dbConnect, a);
		System.out.println("After adding new author...");
		selectAuthors(dbConnect);
		System.out.println("\n");
		
		
		System.out.println("Before Edit/Update Existing information about a Author...");
		selectAuthors(dbConnect);
		System.out.println("\n");
		System.out.println("After Edit/Update Existing Information about an Author...");
		Author b = new Author(2, "Kay", "Beck");
		editInfoAuthor(dbConnect, b);
		selectAuthors(dbConnect);
		System.out.println("\n");
		
		
		System.out.println("Before adding new title for an author...");
		selectAuthors(dbConnect);
		System.out.println("\n");
		System.out.println("Adding new title for an author...");
		float price = (float) 8.41;
		Titles title1 = new Titles("0439139600", "Harry Potter and the Goblet of Fire", 1, 2002, 3, price);
		AuthorISBN authI = new AuthorISBN(7, "0439139600");
		addNewTitle(dbConnect, title1, authI);
		selectSpecificPublisher(dbConnect, 3);
		System.out.println("\n");
		
		System.out.println("Before adding new publisher...");
		selectPublishers(dbConnect);
		Publisher p = new Publisher(16, "Penguin Classics");
		insertPublisher(dbConnect, p);
		System.out.println("\n");
		System.out.println("After adding new publisher...");
		selectPublishers(dbConnect);
		
		
		System.out.println("Before Edit/Update Existing information about a publisher...");
		selectPublishers(dbConnect);
		System.out.println("\n");
		System.out.println("After Edit/Update Existing Information about an publisher...");
		Publisher pub1 = new Publisher(1, "ChangedPublisher");
		editInfoPublisher(dbConnect, pub1);
		selectPublishers(dbConnect);
		System.out.println("\n");
		

	}

	/**
	 * Responsible for closing the database to avoid leakage
	 * @throws SQLException
	 */
	public void close() throws SQLException
	{
		dbConnect.close();
	}

	/*********************************	Database Helper Functions	***************************************/

	/**
	 * Responsible for creating the database. If it doesn't exist, create Books database. Else notify user that database is already created
	 * @param dbExist
	 * @throws SQLException 
	 */
	private void createBooksDatabase(boolean dbExist) throws SQLException
	{
		// If Books Database doesn't exist, create database Books
		if(!dbExist) {
			System.out.println("Creating database...");
			stmt = dbConnect.createStatement();
			String createBooksDatabaseQuery = "CREATE DATABASE Books";
			stmt.executeUpdate(createBooksDatabaseQuery);
			System.out.println("Created " + DB_NAME + " database successfully");
			stmt.close();
		} else {
			// Else Books database exists
			System.out.println("Database: " + DB_NAME + " already exists");
		}


	}

	/**
	 * Creates a table for Books Database
	 * @param tableExists Check if table exists or not
	 * @param tableName The name of the table
	 * @param createTableQuery The sql query to create the table
	 * @throws SQLException
	 */
	public void createTable(boolean tableExists, String tableName, String createTableQuery) throws SQLException
	{
		if(!tableExists) {
			stmt = dbConnect.createStatement();
			stmt.executeUpdate(createTableQuery);
			System.out.println("Created " + tableName + " successfully");
			stmt.close();
		} else {
			System.out.println("Table: " + tableName + " already exists");
		}

	}

	/**
	 * Checks if database exists
	 * @param dbName The database name we want to check if it exists
	 * @return true if exists, false otherwise
	 * @throws SQLException
	 */
	private boolean checkDatabaseExists(String dbName) throws SQLException
	{
		boolean dbExists = false;
		ResultSet resultSet = dbConnect.getMetaData().getCatalogs();
		while(resultSet.next()) {
			String retrievedDBName = resultSet.getString(1);
			if(retrievedDBName.equals(dbName)) {
				dbExists = true;
				return dbExists;
			}
		}
		return dbExists;
	}

	/**
	 * Checks if table exists
	 * @param tableName The table name to we want to check if it exists
	 * @return true if table exists, false otherwise
	 * @throws SQLException
	 */
	private boolean checkTableExists(String tableName) throws SQLException
	{
		boolean tableExists = false;
		ResultSet resultSet = dbConnect.getMetaData().getTables(null, null, tableName, null);
		while(resultSet.next()) {
			String retrievedTableName = resultSet.getString("TABLE_NAME");
			if(retrievedTableName.equals(tableName)) {
				tableExists = true;
				return tableExists;
			}
		} return tableExists;
	}
	

	
	/** 
	 * Code Citation: https://stackoverflow.com/questions/1497569/how-to-execute-sql-script-file-using-jdbc
	 * Responsible for executing sql statements from a .sql file
	 * @param conn Database Connection
	 * @param inputFile The .sql file you want to parse through
	**/
	public void executeSqlScript(Connection conn, File inputFile) {

		// Delimiter
		String delimiter = ";";

		// Create scanner
		Scanner scanner;
		try {
			scanner = new Scanner(inputFile).useDelimiter(delimiter);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}

		// Loop through the SQL file statements 
		Statement currentStatement = null;
		while(scanner.hasNext()) {

			// Get statement 
			String rawStatement = scanner.next() + delimiter;
			try {
				// Execute statement
				currentStatement = conn.createStatement();
				try {
					try {
						currentStatement.execute(rawStatement); 
					} catch (MySQLIntegrityConstraintViolationException e) {
						System.out.println("Values already added to Books Database");
					}
				} catch (MySQLSyntaxErrorException e) {
					System.out.println("Values added to Books Database successfully");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// Release resources
				if (currentStatement != null) {
					try {
						currentStatement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				currentStatement = null;
			}
		}

		scanner.close();
	}
	
	/********** Publisher **********/
	public void insertPublisher(Connection c, Publisher p)
	{
		PreparedStatement preparedStatement = null;

		String insertTableSQL = "INSERT INTO Publisher"
				+ "(publisherID, publisherName) VALUES"
				+ "(?,?)";

		try {
	
			preparedStatement = c.prepareStatement(insertTableSQL);

			preparedStatement.setInt(1, p.getPublisherID());
			preparedStatement.setString(2, p.getPublisherName());

			// execute insert SQL statement
			preparedStatement.executeUpdate();


		} catch (SQLException e) {
			e.printStackTrace();

		}
	}
	public void selectPublishers(Connection c)
	{
		try {
		System.out.println("\n");
		System.out.println("Select All Publishers");
		stmt = c.createStatement();
		String sql = "SELECT * FROM Publisher";
	      ResultSet rs = stmt.executeQuery(sql);
	      //STEP 5: Extract data from result set
	      while(rs.next()){
	         //Retrieve by column name
	         int id  = rs.getInt("publisherID");
	         String name = rs.getString("publisherName");

	         //Display values
	         System.out.print("ID: " + id);
	         System.out.println(", Publisher Name: " + name);
	      }
	      rs.close();
	   }catch (SQLException se){
	      //Handle errors for JDBC
	      se.printStackTrace();
	   }catch(Exception e){
	      //Handle errors for Class.forName
	      e.printStackTrace();
	   }
	}
	
	public void editInfoPublisher(Connection c, Publisher pb){
		PreparedStatement preparedStatement = null;

		String editAuthorSQL = "UPDATE publisher "
				+ "SET publisherName = ? "
				+ "WHERE publisherID = ?";

		try {
	
			preparedStatement = c.prepareStatement(editAuthorSQL);

			preparedStatement.setInt(2, pb.getPublisherID());
			preparedStatement.setString(1, pb.getPublisherName());
			//preparedStatement.setString(3, auth.getLastName());

			// execute insert SQL statement
			preparedStatement.executeUpdate();


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/********** Author **********/
	public void insertAuthor(Connection conn, Author a) throws SQLException 
	{

		PreparedStatement preparedStatement = null;

		String insertTableSQL = "INSERT INTO Author"
				+ "(authorID, firstName, lastName) VALUES"
				+ "(?,?,?)";

		try {
	
			preparedStatement = conn.prepareStatement(insertTableSQL);

			preparedStatement.setInt(1, a.getAuthorID());
			preparedStatement.setString(2, a.getFirstName());
			preparedStatement.setString(3, a.getLastName());

			// execute insert SQL statement
			preparedStatement.executeUpdate();


		} catch (SQLException e) {
			e.printStackTrace();

		}

	}

	public void selectAuthors(Connection c)
	{
		// select * from author order by TRIM(lastName) ASC, TRIM(firstName);
		try {
			System.out.println("\n");
			System.out.println("Select all authors from Author table. Order the information alphabetically by the author's last name and first name");
			stmt = c.createStatement();
			String sql = "select * from author order by TRIM(lastName) ASC, TRIM(firstName) ASC";
		      ResultSet rs = stmt.executeQuery(sql);
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		         String firstName = rs.getString("firstName");
		         String lastName = rs.getString("lastName");

		         //Display values
		         System.out.print(firstName);
		         System.out.println(", " + lastName);
		      }
		      rs.close();
		   }catch (SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }
	}
	
	public void editInfoAuthor(Connection c, Author auth){
		PreparedStatement preparedStatement = null;

		String editAuthorSQL = "UPDATE Author "
				+ "SET firstName = ? "
				+ "WHERE authorID = ?";

		try {
	
			preparedStatement = c.prepareStatement(editAuthorSQL);

			preparedStatement.setInt(2, auth.getAuthorID());
			preparedStatement.setString(1, auth.getFirstName());
			//preparedStatement.setString(3, auth.getLastName());

			// execute insert SQL statement
			preparedStatement.executeUpdate();


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/********** Titles **********/
	public void selectSpecificPublisher(Connection c, int publisherID)
	{
		try {
			System.out.println("\n");
			System.out.println("Select a specific publisher and list all books published by that publisher. Include the title, year and ISBN number. Order the information alphabetically by title. ");
			stmt = c.createStatement();
			String sql = "SELECT isbn, title, year, publisherID FROM titles WHERE publisherID = " + String.valueOf(publisherID)  + " ORDER BY title;";
		      ResultSet rs = stmt.executeQuery(sql);
		      //STEP 5: Extract data from result set
		      while(rs.next()){
		         //Retrieve by column name
		         String isbn = rs.getString("isbn");
		         String title = rs.getString("title");
		         String year = rs.getString("year");
		         int pID = rs.getInt("publisherID");

		         //Display values
		         System.out.print(isbn);
		         System.out.print(", " + title);
		         System.out.print(", " + year);
		         System.out.print(", " + pID + "\n");
		      }
		      rs.close();
		   }catch (SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }
	}
	
	public void addNewTitle(Connection c, Titles t1, AuthorISBN authIsbn){
		PreparedStatement preparedStatement = null;

//		String insertTableSQL = "INSERT INTO authorISBN, "
//				+ "titles(authorISBN.authorID, authorISBN.isbn, "
//				+ "titles.isbn, titles.title, titles.editionNumber, titles.year, titles.publisherID, titles.price) "
//				+ "VALUES (?,?,?,?,?,?,?,?)";
		
		String insertTableSQL = "INSERT INTO "
				+ "titles(isbn, title, editionNumber, year, publisherID, price)"
				+ "VALUES (?,?,?,?,?,?)";
		
		String insertAuthorISBNTableSQL = "INSERT INTO "
				+ "authorISBN(authorID, isbn)"
				+ "VALUES (?,?)";

		try {
	
			preparedStatement = c.prepareStatement(insertTableSQL);

			preparedStatement.setString(1, t1.getIsbn());
			preparedStatement.setString(2, t1.getTitle());
			preparedStatement.setInt(3, t1.getEditionNumber());
			preparedStatement.setInt(4, t1.getYear());
			preparedStatement.setInt(5, t1.getPublisherID());
			preparedStatement.setFloat(6, t1.getPrice());
			
			// execute insert SQL statement
			preparedStatement.executeUpdate();
			
			preparedStatement = c.prepareStatement(insertAuthorISBNTableSQL);
			
			preparedStatement.setInt(1, authIsbn.getAuthorID());
			preparedStatement.setString(2, authIsbn.getIsbn());
			
			// execute insert SQL statement
			preparedStatement.executeUpdate();


		} catch (SQLException e) {
			e.printStackTrace();

		}
		

	}
}

