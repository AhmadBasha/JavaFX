package model.classes;

import java.sql.SQLException;
// Ahmed Basha A Altowairqi
// S3644668
import java.util.ArrayList;

import model.database.DB_access;

import model.exceptions.DuplicateIdException;

import model.helper.DateTime;

public class CityLodge
{
	// here creating a list object of BaseRoom and sc for the user input.
	public ArrayList<BaseRoom> rooms = new ArrayList<>();
//	private static Scanner sc;


	// this method if to check the room if exist or not
	// and checking the room id and compare it with other for renting purpose
	public int getRoomIndex( String inputId )
	{
		if( rooms.isEmpty() )
		{
			return -1;
		}

		int index = -1; // -1 means doesn't exist.
		for( int i = 0; i < rooms.size(); i++ )
		{
			if( inputId.compareTo( rooms.get( i ).getRoomId() ) == 0 )
			{
				index = i; // room found at index i
				break;
			}
		}
		return index;
	}


	// this method to split the date and return the date

	public DateTime getDateFormat( String inputDate )
	{
		String dateList[] = inputDate.split( "/" );
		if( dateList.length != 3 )
		{
			System.out.println( "Invalid Date entered!" );
			return null;
		}
		int day = Integer.parseInt( dateList[0] );
		int month = Integer.parseInt( dateList[1] );
		int year = Integer.parseInt( dateList[2] );

		DateTime date = new DateTime( day, month, year );
		return date;
	}


	public void addRoom( String inputId, String summary, int num, DateTime maintainanceDate, String image )
			throws DuplicateIdException, SQLException
	{
		for( int i = 0; i < rooms.size(); i++ )
		{
			if( inputId.compareTo( rooms.get( i ).getRoomId() ) == 0 )
			{
				throw new DuplicateIdException( inputId );
			}
		}

		if( inputId.charAt( 0 ) == 'R' )
		{
			StdRoom temp = new StdRoom( inputId, num, summary, image );
			DB_access.insertRoom( inputId, num, summary, maintainanceDate, image );
			rooms.add( temp );

		}
		else if( inputId.charAt( 0 ) == 'S' )
		{
			Suite temp = new Suite( inputId, summary, maintainanceDate, image );
			DB_access.insertRoom( inputId, num, summary, maintainanceDate, image );
			rooms.add( temp );
		}
	}


	// this method to display the rooms

	public String getRoomStrings()
	{
		if( rooms.isEmpty() )
		{
			return "";
			// System.out.println("No rooms to display!");
		}
		else
		{
			String str = "";
			for( int i = 0; i < rooms.size(); i++ )
			{
				str += rooms.get( i ).toString();
			}
			return str;
		}
	}


	public void dispRooms()
	{
		if( rooms.isEmpty() )
		{
			System.out.println( "No rooms to display!" );
		}
		else
		{
			for( int i = 0; i < rooms.size(); i++ )
			{
				// rooms.get(i) calls the toString() function by default
				System.out.println( rooms.get( i ).getDetails()
						+ ((i == rooms.size() - 1) ? "" : "\n======================================") );
			}
		}
	}


	public void loadRoom( String id, int numRooms, Status status, String summary, String stringDate, String image,
			ArrayList<HiringRecord> record )
	{
		if( id.charAt( 0 ) == 'R' )
		{
			StdRoom temp = new StdRoom( id, numRooms, status, summary, image, record );
			rooms.add( temp );

		}
		else if( id.charAt( 0 ) == 'S' )
		{
			DateTime maintainanceDate = DateTime.getSQLDateFormat( stringDate );
			Suite temp = new Suite( id, status, summary, maintainanceDate, image, record );
			rooms.add( temp );
		}
	}
}
