package model.classes;

import javafx.util.Pair;
import model.helper.DateTime;

// Ahmed Basha A Altowairqi
// S3644668
public class HiringRecord
{
	private String recordId;
	private DateTime rentDate;
	private DateTime estimateDate;
	private DateTime actualReturnDate;
	private double rentalFee;
	private double lateFee;


	// getter methods

	public String getRecordId()
	{ return recordId; }


	public double getRentalFee()
	{ return rentalFee; }


	public double getLateFee()
	{ return lateFee; }


	public DateTime getRentDate()
	{ return rentDate; }


	public DateTime getEstimatedDate()
	{ return estimateDate; }


	public DateTime getActualDate()
	{ return actualReturnDate; }


	// constructor
	public HiringRecord( String recordId, DateTime rentDate, int numDays )
	{
		this.recordId = recordId;
		this.rentDate = rentDate;
		estimateDate = new DateTime( rentDate, numDays );
	}


	public HiringRecord( String recordId, DateTime rentDate, DateTime estimateDate, DateTime actualReturnDate,
			double rentalFee, double lateFee )
	{
		this.recordId = recordId;
		this.rentDate = rentDate;
		this.estimateDate = estimateDate;
		this.actualReturnDate = actualReturnDate;
		this.rentalFee = rentalFee;
		this.lateFee = lateFee;
	}


	@Override
	public String toString()
	{
		// recordId:rentDate:estimatedReturnDate:actualReturnDate:rentalFee:lateFee
		// ( Condition ) ? (True assignment) : (false assignment);
		String returnStr = recordId + ":" + rentDate + ":" + estimateDate + ":"
				+ ((actualReturnDate == null) ? "none:none:none"
						: (actualReturnDate + ":" + String.format( "%.2f", rentalFee ) + ":"
								+ String.format( "%.2f", lateFee )));
		return returnStr;
	}


	public String getDetails()
	{
		/*
		 * Minimizing string concatenation and reference operations by creating
		 * string in one statement
		 */

		String returnStr = "Record ID:\t\t\t" + recordId + "\nRentDate:\t\t\t\t" + rentDate
				+ "\nEstimated Return Date:\t" + estimateDate
				+ ((actualReturnDate == null) ? ""
						: "\nActual Return Date:\t\t" + actualReturnDate + "\nRental Fee:\t\t\t"
								+ String.format( "%.2f", rentalFee ) + "\nLate Fee:\t\t\t\t"
								+ String.format( "%.2f", lateFee ));
		return returnStr;
	}


	// updating the record

	public Pair<Double, Double> updateRecord( DateTime returnDate, double rate, double lateRate )
	{

		// checking for how many days by the different between return and rent
		// also , to know the extra days .
		int daysStay = DateTime.diffDays( returnDate, rentDate );
		int extraDays = DateTime.diffDays( returnDate, estimateDate );

		// ( Condition ) ? (True assignment) : (false assignment);
		extraDays = extraDays > 0 ? extraDays : 0;
		daysStay -= extraDays;

		double rentalFee = rate * daysStay;
		double lateFee = lateRate * extraDays;

		this.rentalFee = rentalFee;
		this.lateFee = lateFee;
		actualReturnDate = returnDate;

		Pair<Double, Double> retPair = new Pair<>( rentalFee, lateFee );
		return retPair;
	}

}
