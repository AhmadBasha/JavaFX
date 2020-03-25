package model.exceptions;

public class MinDaysException extends Exception
{

	// minumum days chicking 

	public MinDaysException( int minDaysNeeded, int gotDays )
	{
		super( "MinDaysException:  Min days is : " + minDaysNeeded + " you sent : " + gotDays );
	}
}
