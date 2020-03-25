package model.exceptions;

public class MaxDaysException extends Exception
{


	// maximum days checking 
	public MaxDaysException( int maxDaysPossible, int gotDays )
	{
		super( "MinDaysException:  Max days is : " + maxDaysPossible + " you sent : " + gotDays );
	}
}
