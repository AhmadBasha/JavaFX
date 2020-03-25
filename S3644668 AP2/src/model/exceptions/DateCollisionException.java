package model.exceptions;

import model.helper.DateTime;

public class DateCollisionException extends Exception
{
	// here to check the data if previous 
	public DateCollisionException( DateTime prevDate )
	{
		super( "DateCollisionException: Entered date should be after : " + prevDate );
	}
}
