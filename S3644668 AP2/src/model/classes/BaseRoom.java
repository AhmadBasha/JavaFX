package model.classes;

// Ahmed Basha A Altowairqi
// S3644668
import java.util.ArrayList;

import javafx.util.Pair;
import model.exceptions.DateCollisionException;
import model.exceptions.MaintainanceDueException;
import model.exceptions.MaxDaysException;
import model.exceptions.MinDaysException;
import model.exceptions.StatusException;
import model.helper.DateTime;

public abstract class BaseRoom
{
	// all the variables
	protected String roomId;
	protected int noBedrooms;
	protected String summary;
	protected Status roomStatus;
	protected ArrayList<HiringRecord> record = new ArrayList<>();
	protected String image;
	// constructor


	public BaseRoom( String roomId, int noBedrooms, String summary, String image )
	{
		this.roomId = roomId;
		this.noBedrooms = noBedrooms;
		this.summary = summary;
		this.image = image;

		roomStatus = Status.AVAILABLE;
	}


	public BaseRoom( String roomId, int noBedrooms, Status status, String summary, String image,
			ArrayList<HiringRecord> record )
	{
		this.roomId = roomId;
		this.noBedrooms = noBedrooms;
		this.summary = summary;
		this.image = image;
		this.record = record;

		roomStatus = status;
	}


	// this method to change the status of the room to rented .
	// the first condition for checking the rent date with number of days
	// and making a new object of HiringRecord class which have all the
	// parameters
	// values
	// then , check the size of the record if 10 or not to save
	protected boolean addHiringRecord( String custId, DateTime rDate, int numDays ) throws DateCollisionException
	{
		// check if rDate is after the previous return date.
		if( !record.isEmpty() )
		{
			if( DateTime.diffDays( record.get( 0 ).getActualDate(), rDate ) > 0 )
			{
				throw new DateCollisionException( record.get( 0 ).getActualDate() );
			}
		}

		String recordId = roomId + "_" + custId + "_" + rDate.getEightDigitDate();

		HiringRecord temp = new HiringRecord( recordId, rDate, numDays );

		if( record.size() == 10 )
		{
			record.remove( record.size() - 1 );
		}
		record.add( 0, temp );
		roomStatus = Status.RENTED;
		return true;
	}


	// for the room id
	public String getRoomId()
	{ return roomId; }


	// all the method that need for the sub classes

	public abstract void rent( String customerId, DateTime rentDate, int numOfRentDay ) throws StatusException,
			MinDaysException, MaxDaysException, MaintainanceDueException, DateCollisionException;


	public abstract void completeMaintenance( DateTime completionDate ) throws StatusException;


	public abstract Pair<Double, Double> returnRoom( DateTime returnDate ) throws StatusException, MinDaysException;


	@Override
	public abstract String toString();


	public abstract String getDetails();


	public abstract double getRate();


	// check the status
	public void performMaintenance() throws StatusException
	{
		if( roomStatus != Status.AVAILABLE )
		{
			throw new StatusException( Status.AVAILABLE, roomStatus );
		}

		roomStatus = Status.MAINTENANCE;
	}


	// check the status
	public boolean isAvailable()
	{
		if( roomStatus != Status.AVAILABLE )
		{
			return false;
		}
		return true;
	}


	// for the last record
	public HiringRecord printLastRecord()
	{
		return (record.get( 0 ));
	}


	// get all record.
	public String getHiringRecordString()
	{
		String str = "";

		for( HiringRecord rec : record )
		{
			str += rec.toString() + "\n";
		}

		return str;
	}
}
