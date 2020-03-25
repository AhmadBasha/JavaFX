package model.exceptions;

public class MyException extends Exception
{


	// showing the error message and used on the controller packge 
	public MyException( String msg )
	{
		super( msg );
	}
}
