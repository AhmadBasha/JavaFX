package model.exceptions;

import model.classes.Status;

public class StatusException extends Exception
{

	// Here to Check if the Status of the room  .. for example to let the user rent the room which is avaliable

	public StatusException( Status required, Status current )
	{
		super( "StatusException: needed status = \'" + required + "\', found status = \'" + current + "\'" );
	}
}
