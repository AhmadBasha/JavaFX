package model.exceptions;

import model.helper.DateTime;

public class MaintainanceDueException extends Exception
{
	// checking the date of the maintainance due

	public MaintainanceDueException( DateTime nextDate )
	{
		super( "MaintainanceDueException: Maintainance due on: " + nextDate );
	}
}
