package controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import model.database.DB_access;
import model.exceptions.DateCollisionException;
import model.exceptions.MaintainanceDueException;
import model.exceptions.MaxDaysException;
import model.exceptions.MinDaysException;
import model.exceptions.MyException;
import model.exceptions.StatusException;
import model.helper.DateTime;
import view.Main;

public class RoomViewController
{
	// fields to update
	
	public Label roomId;
	public Label numRooms;
	public Label summary;
	public Label status;
	public Label rate;
	
	// image container.
	public Pane imagePane;

	
	// set the fields accordingly
	public void setLabels( String roomId, String numRooms, String summary, String status, String rate,
			String imageName )
	{
		this.roomId.setText( roomId );
		this.numRooms.setText( numRooms );
		this.summary.setText( summary );
		this.status.setText( status );
		this.rate.setText( rate );

		Image image;
		try
		{
			// loading file
			image = new Image( new FileInputStream( "images/" + imageName ) );
			// Setting the image view
			ImageView imageView = new ImageView( image );

			// setting the fit height and width of the image view
			imageView.setFitHeight( 150 );
			imageView.setFitWidth( 150 );
			
			// Setting the preserve ratio of the image view
			imageView.setPreserveRatio( true );
			
			// add image to pane
			imagePane.getChildren().add( imageView );
		}
		catch( FileNotFoundException e )
		{
			// add notice to image pane
			Label notFound = new Label( "Image Not Found" );
			notFound.setLayoutY( 60.0 );
			notFound.setLayoutX( 10.0 );
			imagePane.getChildren().add( notFound );
		}
	}

	// rent button pressed
	public void handleRent()
	{
		// get the id
		String rId = roomId.getText();
		
		// need to get three inputs from dialog
		Dialog<Pair<String, Pair<DateTime, Integer>>> dialog = new Dialog<>();
		dialog.setTitle( "Room Rent" );
		dialog.setHeaderText( "For room: " + rId );

		TextField cId = new TextField();
		cId.setPromptText( "C_xxx" );
		DatePicker rDate = new DatePicker();
		rDate.setPromptText( "MM/DD/YYYY" );
		TextField numDaysField = new TextField();
		numDaysField.setPromptText( "1 - 10" );

		GridPane grid = new GridPane();
		grid.setHgap( 10 );
		grid.setVgap( 10 );
		grid.setPadding( new Insets( 10, 10, 10, 10 ) );
		grid.add( new Label( "Enter Customer ID:" ), 0, 0 );
		grid.add( cId, 1, 0 );
		grid.add( new Label( "Enter Rent Date:" ), 0, 1 );
		grid.add( rDate, 1, 1 );
		grid.add( new Label( "Number of days:" ), 0, 2 );
		grid.add( numDaysField, 1, 2 );

		dialog.getDialogPane().getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );
		dialog.getDialogPane().setContent( grid );

		dialog.setResultConverter( button->
		{
			if( button == ButtonType.OK )
			{
				// get the results
				String idTemp = cId.getText();
				LocalDate rTemp = rDate.getValue();
				String numTemp = numDaysField.getText();
				
				// check if anything is null
				if( idTemp == null || rTemp == null || numTemp == null )
				{
					return null;
				}
				
				// get the date in correct format
				DateTime dateTemp = new DateTime( rTemp.getDayOfMonth(), rTemp.getMonthValue(), rTemp.getYear() );
				
				// create the PAIR
				Pair<DateTime, Integer> pTemp = new Pair<>( dateTemp, Integer.parseInt( numTemp ) );
				
				// Create and return the 3-Pair
				return new Pair<>( idTemp, pTemp );
			}
			return null;
		} );
		
		Optional<Pair<String, Pair<DateTime, Integer>>> result = dialog.showAndWait();
		
		if( result.isPresent() )
		{
			try
			{
				// decouple results
				Pair<String, Pair<DateTime, Integer>> p1 = result.get();
				Pair<DateTime, Integer> p2 = p1.getValue();

				String custId = p1.getKey();
				
				// can't have underscore.
				if( custId.indexOf( '_' ) != -1 )
				{
					throw new MyException( "CustomerId can't have an underscore!" );
				}
				
				
				DateTime rentDate = p2.getKey();
				int numDays = p2.getValue();
				DateTime estimDate = new DateTime( rentDate, numDays );
				
				// get index of the room
				int index = Main.cityLodge1.getRoomIndex( rId );
				
				// rent
				Main.cityLodge1.rooms.get( index ).rent( custId, rentDate, p2.getValue() );
				
				// update db if successful
				DB_access.updateStatus( rId, 'R' );
				DB_access.insertRecord( rId, custId, rentDate, estimDate );
				
				// refresh
				Main.mainCtrl.refresh();
			}
			catch( StatusException | MinDaysException | MaxDaysException | MaintainanceDueException
					| DateCollisionException | MyException e )
			{
				// display errors
				Alert error = new Alert( Alert.AlertType.ERROR );
				error.setTitle( "Error Occured" );
				error.setHeaderText( "For room: " + rId );
				error.setContentText( e.getMessage() );
				error.showAndWait();
			}
		}
	}

	// for return

	public void handleReturn()
	{
		// similar as before
		String rId = roomId.getText();
		Dialog<DateTime> dialog = new Dialog<>();
		dialog.setTitle( "Return Room" );
		dialog.setHeaderText( "For room: " + rId );

		DatePicker rDate = new DatePicker();
		rDate.setPromptText( "MM/DD/YYYY" );
		GridPane grid = new GridPane();
		grid.setHgap( 10 );
		grid.setPadding( new Insets( 10, 10, 10, 10 ) );
		grid.add( new Label( "Enter Return Date:" ), 0, 0 );
		grid.add( rDate, 1, 0 );

		dialog.getDialogPane().getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );
		dialog.getDialogPane().setContent( grid );

		dialog.setResultConverter( button->
		{
			if( button == ButtonType.OK )
			{
				LocalDate temp = rDate.getValue();
				if( temp == null )
				{
					return null;
				}
				return new DateTime( temp.getDayOfMonth(), temp.getMonthValue(), temp.getYear() );
			}
			return null;
		} );

		Optional<DateTime> result = dialog.showAndWait();

		if( result.isPresent() )
		{
			try
			{
				int index = Main.cityLodge1.getRoomIndex( rId );
				DateTime returnDate = result.get();

				Pair<Double, Double> fees = Main.cityLodge1.rooms.get( index ).returnRoom( returnDate );

				DB_access.updateStatus( rId, 'A' );
				DB_access.updateRecord( rId, returnDate, fees.getKey(), fees.getValue() );

				Main.mainCtrl.refresh();
			}
			catch( StatusException | MinDaysException e )
			{
				Alert error = new Alert( Alert.AlertType.ERROR );
				error.setTitle( "Error Occured" );
				error.setHeaderText( "For room: " + rId );
				error.setContentText( e.getMessage() );
				error.showAndWait();
			}
		}
	}

	// for details 

	public void handleDetails()
	{
		// show details in a dialogue
		int index = Main.cityLodge1.getRoomIndex( roomId.getText() );

		Alert alert = new Alert( Alert.AlertType.INFORMATION );

		alert.setTitle( "Room Details" );
		alert.setHeaderText( "For room: " + roomId.getText() );
		alert.setContentText( Main.cityLodge1.rooms.get( index ).getDetails() );

		alert.showAndWait();
	}


	public void handlePerformM()
	{
		// confirm action and perform maintainance
		String rId = roomId.getText();
		Alert alert = new Alert( Alert.AlertType.CONFIRMATION );
		alert.setTitle( "Perform Maintainance" );
		alert.setHeaderText( "For room: " + rId );
		alert.setContentText( "Confirm ?" );

		Optional<ButtonType> result = alert.showAndWait();
		if( result.get() == ButtonType.OK )
		{
			try
			{
				int index = Main.cityLodge1.getRoomIndex( rId );
				Main.cityLodge1.rooms.get( index ).performMaintenance();

				DB_access.updateStatus( rId, 'M' );

				Main.mainCtrl.refresh();
			}
			catch( StatusException e )
			{
				Alert error = new Alert( Alert.AlertType.ERROR );
				error.setTitle( "Error Occured" );
				error.setHeaderText( "For room: " + rId );
				error.setContentText( e.getMessage() );
				error.showAndWait();
			}
		}
	}

	// for complete maintainance

	public void handleCompleteM()
	{
		// if normal room. no date needed
		// else get date and complete maintainance
		String rId = roomId.getText();
		if( rId.charAt( 0 ) == 'R' )
		{
			Alert alert = new Alert( Alert.AlertType.CONFIRMATION );
			alert.setTitle( "Complete Maintainance" );
			alert.setHeaderText( "For room: " + rId );
			alert.setContentText( "Confirm ?" );

			Optional<ButtonType> result = alert.showAndWait();
			if( result.get() == ButtonType.OK )
			{
				try
				{
					int index = Main.cityLodge1.getRoomIndex( rId );
					Main.cityLodge1.rooms.get( index ).completeMaintenance( null );

					DB_access.updateStatus( rId, 'A' );

					Main.mainCtrl.refresh();
				}
				catch( StatusException e )
				{
					Alert error = new Alert( Alert.AlertType.ERROR );
					error.setTitle( "Error Occured" );
					error.setHeaderText( "For room: " + rId );
					error.setContentText( e.getMessage() );
					error.showAndWait();
				}
			}
		}
		else
		{
			Dialog<DateTime> dialog = new Dialog<>();
			dialog.setTitle( "Complete Maintainance" );
			dialog.setHeaderText( "For room: " + rId );

			DatePicker cDate = new DatePicker();
			cDate.setPromptText( "MM/DD/YYYY" );
			GridPane grid = new GridPane();
			grid.setHgap( 10 );
			grid.setPadding( new Insets( 10, 10, 10, 10 ) );
			grid.add( new Label( "Enter Completion Date:" ), 0, 0 );
			grid.add( cDate, 1, 0 );

			dialog.getDialogPane().getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );
			dialog.getDialogPane().setContent( grid );

			dialog.setResultConverter( button->
			{
				if( button == ButtonType.OK )
				{
					LocalDate temp = cDate.getValue();
					if( temp == null )
					{
						return null;
					}
					return new DateTime( temp.getDayOfMonth(), temp.getMonthValue(), temp.getYear() );
				}
				return null;
			} );

			Optional<DateTime> result = dialog.showAndWait();

			if( result.isPresent() )
			{
				try
				{
					int index = Main.cityLodge1.getRoomIndex( rId );
					Main.cityLodge1.rooms.get( index ).completeMaintenance( result.get() );

					DB_access.updateStatus( rId, 'A' );
					DB_access.updateMaintainanceDate( rId, result.get() );

					Main.mainCtrl.refresh();
				}
				catch( StatusException e )
				{
					Alert error = new Alert( Alert.AlertType.ERROR );
					error.setTitle( "Error Occured" );
					error.setHeaderText( "For room: " + rId );
					error.setContentText( e.getMessage() );
					error.showAndWait();
				}
			}
		}
	}
}
