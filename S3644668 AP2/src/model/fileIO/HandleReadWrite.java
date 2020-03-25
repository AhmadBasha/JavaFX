package model.fileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import model.classes.BaseRoom;
import model.classes.HiringRecord;
import model.classes.Status;
import model.database.DB_access;
import model.exceptions.MyException;
import model.helper.DateTime;
import view.Main;

// for reading and writing
public class HandleReadWrite
{
	// write data
	public static void writeToFile( String folder ) throws IOException
	{
		// here the file name that we will find in our computer. 
		String fileName = "export_data.txt";

		FileWriter writer = null;

		writer = new FileWriter( folder + fileName );
		
		// Is a loop for writing on the file. 
		// for all rooms in memory
		for( BaseRoom r : Main.cityLodge1.rooms )
		{
			// Write room details on the file 
			writer.write( r.toString() + "\n" );
			
			// then write the hiring records
			writer.write( r.getHiringRecordString() );
		}
		// closing the file . 
		writer.close();
	}

	// read data that insert to the program.
	public static void readFromFile( File readFile ) throws MyException
	{
		Scanner reader = null;
		try
		{
			reader = new Scanner( readFile );
			if( reader.hasNext() )
			{
				// clear database and this is will make the database empty. 
				DB_access.clearDatabase();
				
				// clear memory. to save the new item that will be exist on the program. 
				Main.cityLodge1.rooms.clear();
				
				// make many variables to save room details. 
				String roomId = null;
				int noBedrooms = 0;
				String summary = null;
				Status roomStatus = null;
				char setStatus;
				ArrayList<HiringRecord> record = new ArrayList<>();
				String image = null;
				DateTime lmDate = null;
				
				// save number for reference
				int num = 0;
				
				// start reading line by line
				while( reader.hasNext() )
				{
					// read one line, and split from commas
					String line = reader.nextLine();

					// Spearating the line by :
					String seperated[] = line.split( ":" );
					
					// id is at first location
					String id = seperated[0];
					
					// here to make sure all the value that entered are true. 
					// check if id has '_'  
					if( id.indexOf( '_' ) == -1 ) 
					{
						// no '_' so it is a room
						if( num > 0 )
						{
							// save the previously loaded room
							Main.cityLodge1.loadRoom( roomId, noBedrooms, roomStatus, summary,
									(lmDate != null ? lmDate.getSqlDate() : null), image, record );
							
							// add the hiring records to database in reverse order
							for( int i = record.size() - 1; i >= 0; --i )
							{
								HiringRecord rec = record.get( i );

								String combinedId = rec.getRecordId();
								String[] splitId = combinedId.split( "_" );
								String cId = splitId[1];

								DB_access.insertRecord( roomId, cId, rec.getRentDate(), rec.getEstimatedDate() );

								if( rec.getActualDate() != null )
								{
									DB_access.updateRecord( roomId, rec.getActualDate(), rec.getRentalFee(),
											rec.getLateFee() );
								}
							}
							
							// clear the used variables for next room
							roomId = null;
							noBedrooms = 0;
							summary = null;
							roomStatus = null;
							setStatus = '\0';
							record = new ArrayList<>();
							image = null;
							lmDate = null;

						}
						
						// get the room details
						roomId = id;
						noBedrooms = Integer.parseInt( seperated[1] );

						setStatus = seperated[3].toUpperCase().charAt( 0 );
						roomStatus = setStatus == 'R' ? Status.RENTED
								: (setStatus == 'M' ? Status.MAINTENANCE : Status.AVAILABLE);

						image = seperated[seperated.length - 1];
						summary = seperated[seperated.length - 2];

						if( roomId.charAt( 0 ) == 'S' )
						{
							lmDate = Main.cityLodge1.getDateFormat( seperated[seperated.length - 3] );
						}
						
						// insert room into db
						DB_access.insertRoom( id, noBedrooms, summary, lmDate, image );
						
						// correct the status that was inserted
						DB_access.updateStatus( id, setStatus );

						num++;
					}
					else
					{
						// line is a record.
						// get details and add record to temp record
						DateTime rentDate = Main.cityLodge1.getDateFormat( seperated[1] );
						DateTime estimDate = Main.cityLodge1.getDateFormat( seperated[2] );

						if( seperated[3].equalsIgnoreCase( "none" ) )
						{
							record.add( new HiringRecord( id, rentDate, estimDate, null, 0, 0 ) );
						}
						else
						{
							DateTime actualDate = Main.cityLodge1.getDateFormat( seperated[3] );
							double rentalFee = Double.parseDouble( seperated[4] );
							double lateFee = Double.parseDouble( seperated[5] );
							record.add( new HiringRecord( id, rentDate, estimDate, actualDate, rentalFee, lateFee ) );
						}
					}
				}
				// finally, outside the loop, write the last room details to memory
				Main.cityLodge1.loadRoom( roomId, noBedrooms, roomStatus, summary,
						(lmDate != null ? lmDate.getSqlDate() : null), image, record );
			}
		}
		// any errors that happend. 
		catch( FileNotFoundException e )
		{
			throw new MyException( "FileNotFoundException: File is not found!" );
		}
		catch( NumberFormatException e )
		{
			throw new MyException( "NumberFormatException: File is not formatted correctly!" );
		}
		catch( ArrayIndexOutOfBoundsException e )
		{
			throw new MyException( "ArrayIndexOutOfBoundsException: File is not formatted correctly!" );
		}
		finally
		{
			// close the reader file.
			if( reader != null )
			{
				reader.close();
			}
		}
	}
}
