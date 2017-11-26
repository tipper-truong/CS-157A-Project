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
}

