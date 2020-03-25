package model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_Init
{

	private final static String DB_NAME = "CityLodge";
	private final static String user = "CL";
	private final static String pass = "";

	private static Connection con;

	// run program only once. 
	public static void main( String[] args )
	{
		// use try-with-resources Statement and checking the connection 
		try( Connection con = getConnection() )
		{
			System.out.println( "Connection to database " + DB_NAME + " created successfully" );
            // creating the room table that will save all the room data.
			Statement stmt = con.createStatement();
			int result = stmt.executeUpdate( "CREATE TABLE room (id VARCHAR(7),"
					+ " numRooms INT, status CHAR(1), summary VARCHAR(20), lastMainDate DATE, image VARCHAR(20), PRIMARY KEY (id))" );
			if( result == 0 )
			{
				System.out.println( "Table 'room' has been created successfully" );
				 // creating the record table that will save all the detailes that related with the room 
				stmt = con.createStatement();
				result = stmt.executeUpdate( "CREATE TABLE record (rId VARCHAR(7), cId VARCHAR(7), "
						+ " rentDate DATE, estimDate DATE, actualDate DATE, rentalFee DOUBLE, lateFee DOUBLE,"
						+ " FOREIGN KEY (rId) REFERENCES room( id ), PRIMARY KEY (rId, cId, rentDate))" );
				if( result == 0 )
				{
					System.out.println( "Table 'record' has been created successfully" );
				}
				else
				{
					System.out.println( "Table 'record' is not created" );
				}
			}
			else
			{
				System.out.println( "Table 'room' is not created" );
			}

		}
		catch( Exception e )
		{
			System.out.println( e.getMessage() );
		}
	}

	// return the connection object
	public static Connection getConnection()
	{

		if( con == null )
		{
			try
			{
				// Registering the HSQLDB JDBC driver
				Class.forName( "org.hsqldb.jdbc.JDBCDriver" );
				con = DriverManager.getConnection( "jdbc:hsqldb:file:database/" + DB_NAME, user, pass );
			}
			catch( ClassNotFoundException | SQLException e )
			{
				e.printStackTrace();
			}
		}

		return con;
	}
}
