package model.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import model.classes.HiringRecord;
import model.classes.Status;
import model.helper.DateTime;
import view.Main;

public class DB_access
{
	// this method because when i import the text file . The program will clear the database.
	// to delete all the data
	public static void clearDatabase()
	{
		Connection con = DB_Init.getConnection();

		try
		{
			// deleting records
			String sql = "DELETE FROM record";
			Statement stmt;
			stmt = con.createStatement();
			stmt.executeUpdate( sql );
			
			// deleting rooms
			sql = "DELETE FROM room";
			stmt = con.createStatement();
			stmt.executeUpdate( sql );
		}
		catch( SQLException e )
		{
			System.out.println("SQL Error deleting db");
		}
	}

	//this method just to update the statue of the room. 
	public static void updateStatus( String id, char stat )
	{
		Connection con = DB_Init.getConnection();

		String sql = "UPDATE room SET status = '" + stat + "' WHERE id = '" + id + "'";

		try
		{
			Statement stmt;
			stmt = con.createStatement();
			stmt.executeUpdate( sql );
		}
		catch( SQLException e )
		{
			System.out.println("SQL Error updating status");
		}
	}

	// Here when the new room that the user that added .. will be adding to the database. 

	public static void insertRoom( String id, int numRooms, String summary, DateTime lastMainDate, String image )
	{
		Connection con = DB_Init.getConnection();

		//here wanna to insert the data by using PreparedStatement 
		String sql = "INSERT INTO room Values(?, ?, ?, ?, ?, ?)";

		PreparedStatement prepStat;
		try
		{
			prepStat = con.prepareStatement( sql );
			prepStat.setString( 1, id );
			prepStat.setString( 2, numRooms + "" );
			prepStat.setString( 3, "A" );
			prepStat.setString( 4, summary );
			// if condition for last data .. because the standard rooms do not have this option. 
			if( lastMainDate != null )
			{
				prepStat.setString( 5, "DATE '" + lastMainDate.getSqlDate() + "'" );
			}
			else
			{
				prepStat.setNull( 5, java.sql.Types.DATE );
			}
			prepStat.setString( 6, image );

			prepStat.executeUpdate();
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}

	}

    // This method for inserting the data for the renatl record table . 
	public static void insertRecord( String rId, String cId, DateTime rentDate, DateTime estimDate )
	{
		Connection con = DB_Init.getConnection();
		//here wanna to insert the data by using PreparedStatement 
		String sql = "INSERT INTO record Values(?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepStat;
		try
		{
			prepStat = con.prepareStatement( sql );
			prepStat.setString( 1, rId );
			prepStat.setString( 2, cId );
			prepStat.setString( 3, "DATE '" + rentDate.getSqlDate() + "'" );
			prepStat.setString( 4, "DATE '" + estimDate.getSqlDate() + "'" );
			prepStat.setNull( 5, java.sql.Types.DATE );
			prepStat.setNull( 6, java.sql.Types.DOUBLE );
			prepStat.setNull( 7, java.sql.Types.DOUBLE );

			prepStat.executeUpdate();
		}
		catch( SQLException e )
		{
			System.out.println("SQL Error inserting record");
		}
	}

    // Here to get data rooms from room tables that have created before.
	public static void getRooms() throws SQLException
	{
		Connection con = DB_Init.getConnection();
		String sql = "SELECT * FROM room";

		Statement stmt = con.createStatement();

		ResultSet result = stmt.executeQuery( sql );

		while( result.next() )
		{
			//Here to know the status of the room .
			Status status = null;
			switch( result.getString( "status" ) )
			{
				case "A":
					status = Status.AVAILABLE;
					break;

				case "M":
					status = Status.MAINTENANCE;
					break;

				case "R":
					status = Status.RENTED;
					break;
			}

			Date lastMainDate = result.getDate( "lastMainDate" );

			sql = "SELECT * FROM record WHERE rId ='" + result.getString( "id" ) + "' ORDER BY rentDate;";

			ResultSet rData = stmt.executeQuery( sql );

			ArrayList<HiringRecord> record = new ArrayList<>();

			while( rData.next() )
			{
				DateTime rentDate = DateTime.getSQLDateFormat( rData.getDate( "rentDate" ).toString() );
				DateTime estimatedDate = DateTime.getSQLDateFormat( rData.getDate( "estimDate" ).toString() );
				DateTime actualReturnDate = null;
				double rentalFee = -1;
				double lateFee = -1;
				try
				{
					actualReturnDate = DateTime.getSQLDateFormat( rData.getDate( "actualDate" ).toString() );
					rentalFee = rData.getDouble( "rentalFee" );
					lateFee = rData.getDouble( "lateFee" );
				}
				catch( NullPointerException e )
				{}
				String recordId = rData.getString( "rId" ) + "_" + rData.getString( "cId" ) + "_"
						+ rentDate.getEightDigitDate();

				record.add(
						new HiringRecord( recordId, rentDate, estimatedDate, actualReturnDate, rentalFee, lateFee ) );
			}

			Main.cityLodge1.loadRoom( result.getString( "id" ), result.getInt( "numRooms" ), status,
					result.getString( "summary" ), lastMainDate != null ? lastMainDate.toString() : null,
					result.getString( "image" ), record );
		}
	}


	// Here to update the data of the last maintainence data.  
	public static void updateMaintainanceDate( String id, DateTime lmDate )
	{
		Connection con = DB_Init.getConnection();

		String sql = "UPDATE room SET lastMainDate = DATE'" + lmDate.getSqlDate() + "' WHERE id = '" + id + "'";

		try
		{
			Statement stmt;
			stmt = con.createStatement();
			stmt.executeUpdate( sql );
		}
		catch( SQLException e )
		{
			System.out.println("SQL Error updating date");
		}
	}

	// here updating the rental record. 
	public static void updateRecord( String rId, DateTime returnDate, double rentalFee, double lateFee )
	{
		Connection con = DB_Init.getConnection();

		String sql = "UPDATE record SET actualDate = DATE'" + returnDate.getSqlDate() + "' , rentalFee = " + rentalFee
				+ " , lateFee = " + lateFee + " WHERE rId = '" + rId + "' AND actualDate IS NULL";

		try
		{
			Statement stmt;
			stmt = con.createStatement();
			stmt.executeUpdate( sql );
		}
		catch( SQLException e )
		{
			System.out.println("SQL Error updating record");
		}
	}
}
