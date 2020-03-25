package model.exceptions;

public class DuplicateIdException extends Exception
{
	// if the user a room id is duplicated 

	public DuplicateIdException( String id )
	{
		super( "DuplicateIdException: Room Id  \'" + id + "\' Already exists !" );
	}
}
