package model.classes;

import java.util.ArrayList;

import javafx.util.Pair;
import model.exceptions.DateCollisionException;
import model.exceptions.MaxDaysException;
import model.exceptions.MinDaysException;
import model.exceptions.StatusException;
import model.helper.DateTime;

// Ahmed Basha A Altowairqi
// S3644668
public class StdRoom extends BaseRoom
{
	// all the values
	private static final double pricePerDay1 = 59.0;
	private static final double pricePerDay2 = 99.0;
	private static final double pricePerDay4 = 199.0;
	private static final double latePricePercentage = 1.350;


	// constructor
	public StdRoom( String roomId, int noBedrooms, String summary, String image )
	{
		super( roomId, noBedrooms, summary, image );
	}


	public StdRoom( String roomId, int noBedrooms, Status status, String summary, String image,
			ArrayList<HiringRecord> record )
	{
		super( roomId, noBedrooms, status, summary, image, record );
	}


	// calculating the minimum days and for weekend condition
	private int calcMinDays( DateTime rDate )
	{
		String day = rDate.getNameOfDay();
		int minDays = 2;
		switch( day )
		{
			case "Saturday":
			case "Sunday":
				minDays = 3;
				break;
		}
		return minDays;
	}


	/*
	 * this method for renting by passing three parameters and checking the
	 * status of the room if available to rent or not and checking the minimum
	 * days by calcMinDays() method Also , checking if the days are more than 10
	 * or not and then will pass the values to add HiringRecord () method
	 * @see BaseRoom#rent(java.lang.String, DateTime, int)
	 */

	@Override
	public void rent( String customerId, DateTime rentDate, int numOfRentDay )
			throws StatusException, MinDaysException, MaxDaysException, DateCollisionException
	{
		if( roomStatus != Status.AVAILABLE )
		{
			throw new StatusException( Status.AVAILABLE, roomStatus );
		}
		int minDays = calcMinDays( rentDate );
		if( numOfRentDay < minDays )
		{
			throw new MinDaysException( minDays, numOfRentDay );
		}
		else if( numOfRentDay >= 10 )
		{
			throw new MaxDaysException( 10, numOfRentDay );
		}

		addHiringRecord( customerId, rentDate, numOfRentDay );
		return;
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

		roomStatus = Status.AVAILABLE;
	}


	/*
	 * Checking the status of the room if rented to return or not . and make
	 * sure for how many days the visitor have stayed with looking as well of
	 * the weekend condition .. after that , checking how many best for
	 * calculating to update the hire record The last step change the status to
	 * available
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
		int minDays = calcMinDays( lastRentDate );

		if( daysStay < minDays )
		{
			throw new MinDaysException( minDays, daysStay );
		}

		double rate = getRate();
		double lateRate = rate * StdRoom.latePricePercentage;

		roomStatus = Status.AVAILABLE;
		return record.get( 0 ).updateRecord( returnDate, rate, lateRate );
	}


	/*
	 * i think is so clear
	 * @see BaseRoom#toString()
	 */

	@Override
	public String toString()
	{
		String returnStr = roomId + ":" + noBedrooms + ":Standard:" + roomStatus + ":" + summary;
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
				+ "\nType:\t\t\t\tStandard\nStatus:\t\t\t\t" + roomStatus + "\nFeature Summary:\t\t" + summary
				+ "\nRENTAL RECORD";
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
	{
		double rate = 0;
		switch( noBedrooms )
		{
			case 1:
				rate = StdRoom.pricePerDay1;
				break;
			case 2:
				rate = StdRoom.pricePerDay2;
				break;
			case 4:
				rate = StdRoom.pricePerDay4;
		}

		return rate;
	}

}
