package model.classes;
// Ahmed Basha A Altowairqi
// S3644668

import java.util.ArrayList;

import javafx.util.Pair;
import model.exceptions.DateCollisionException;
import model.exceptions.MaintainanceDueException;
import model.exceptions.MinDaysException;
import model.exceptions.StatusException;
import model.helper.DateTime;

public class Suite extends BaseRoom
{
	// all the values
	private static final int SUITE_SIZE = 6;
	private static final double pricePerDay = 999.0;
	private static final double latePricePerDay = 1099.0;
	private DateTime lastMaintenanceDate;


	// constructor
	public Suite( String roomId, String summary, DateTime lastMaintenanceDate, String image )
	{
		super( roomId, SUITE_SIZE, summary, image );
		this.lastMaintenanceDate = lastMaintenanceDate;
	}


	public Suite( String roomId, Status status, String summary, DateTime lastMaintenanceDate, String image,
			ArrayList<HiringRecord> record )
	{
		super( roomId, SUITE_SIZE, status, summary, image, record );
		this.lastMaintenanceDate = lastMaintenanceDate;
	}


	/*
	 * taking three parameters and checking the statues if can be rented or not
	 * for the if condition is about that suites must have a maintenance
	 * interval of 10 days. then it goes to addHiringRecord method .
	 * @see BaseRoom#rent(java.lang.String, DateTime, int)
	 */

	@Override
	public void rent( String customerId, DateTime rentDate, int numOfRentDay )
			throws StatusException, MaintainanceDueException, DateCollisionException
	{
		if( roomStatus != Status.AVAILABLE )
		{
			throw new StatusException( Status.AVAILABLE, roomStatus );
		}

		if( DateTime.diffDays( rentDate, lastMaintenanceDate ) > (10 - numOfRentDay) )
		{
			throw new MaintainanceDueException( new DateTime( lastMaintenanceDate, 10 ) );
		}

		addHiringRecord( customerId, rentDate, numOfRentDay );
	}


	/*
	 * i think is so clear
	 * @see BaseRoom#completeMaintenance(DateTime)
	 */

	@Override
	public void completeMaintenance( DateTime completionDate ) throws StatusException
	{
		if( roomStatus != Status.MAINTENANCE )
		{
			throw new StatusException( Status.MAINTENANCE, roomStatus );
		}

		lastMaintenanceDate = completionDate;
		roomStatus = Status.AVAILABLE;
	}


	/*
	 * checking the status of the room if not rented so it goes to if condition
	 * then check if the return before the rent date or not then goes to
	 * updateRecord method to pass three values and in the end changing the
	 * status
	 * @see BaseRoom#returnRoom(DateTime)
	 */

	@Override
	public Pair<Double, Double> returnRoom( DateTime returnDate ) throws StatusException, MinDaysException
	{
		if( roomStatus != Status.RENTED )
		{
			throw new StatusException( Status.RENTED, roomStatus );
		}
		DateTime lastRentDate = record.get( 0 ).getRentDate();

		int daysStay = DateTime.diffDays( returnDate, lastRentDate );
		if( daysStay < 0 )
		{
			throw new MinDaysException( 0, daysStay );
		}
		roomStatus = Status.AVAILABLE;
		return record.get( 0 ).updateRecord( returnDate, getRate(), Suite.latePricePerDay );
	}


	/*
	 * i think is so clear
	 * @see BaseRoom#toString()
	 */

	@Override
	public String toString()
	{
		String returnStr = roomId + ":" + noBedrooms + ":Suite:" + roomStatus + ":" + lastMaintenanceDate + ":"
				+ summary;
		returnStr += ":" + image;
		return returnStr;
	}


	/*
	 * here when the user choose option 6 from the main , it should show what
	 * below if there is no rental record so will print empty if not so will
	 * check in getDetails() in HiringRecord class and return the value
	 * @see BaseRoom#getDetails()
	 */

	@Override
	public String getDetails()
	{
		String returnStr = "Room ID:\t\t\t\t" + roomId + "\nNumber of bedrooms:\t" + noBedrooms
				+ "\nType:\t\t\t\tSuite\nStatus:\t\t\t\t" + roomStatus + "\nLast maintenance date:\t"
				+ lastMaintenanceDate + "\nFeature Summary:\t\t" + summary + "\nRENTAL RECORD";
		if( record.isEmpty() )
		{
			returnStr += ":\t\tempty";
		}
		else
		{
			for( int i = 0; i < record.size(); ++i )
			{
				returnStr += ("\n" + record.get( i ).getDetails()
						+ ((i == record.size() - 1) ? "" : "\n--------------------------------------"));
			}
		}
		return returnStr;
	}


	@Override
	public double getRate()
	{ return Suite.pricePerDay; }

}
