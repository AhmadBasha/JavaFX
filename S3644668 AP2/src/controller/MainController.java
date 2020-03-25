package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.classes.BaseRoom;
import model.exceptions.MyException;
import view.Main;

public class MainController
{
	// box where rooms exist
	public VBox roomsPane;
	


	public void initialize()
	{
		refresh();
	}


	public void refresh()
	{
		ArrayList<BaseRoom> rooms = Main.cityLodge1.rooms;

		// To clear the existing window, getChildren and clear.
		roomsPane.getChildren().clear();
		
		// for all rooms
		for( int i = 0; i < rooms.size(); i++ )
		{
			try
			{
				// load room view
				FXMLLoader loader = new FXMLLoader(
						getClass().getClassLoader().getResource( "view/fxml/roomView.fxml" ) );
				AnchorPane anchor = loader.load();
				
				// get controller
				RoomViewController ctrl = loader.getController();
				
				// get string
				String roomStr = rooms.get( i ).toString();
				
				// split string.
				String splitStr[] = roomStr.split( ":" );
				
				// update labels of controller
				ctrl.setLabels( splitStr[0], splitStr[1], splitStr[splitStr.length - 2], splitStr[3],
						"$ " + rooms.get( i ).getRate() + " per night", splitStr[splitStr.length - 1] );
				
				// add the anchor pane to the window
				roomsPane.getChildren().add( anchor );

			}
			catch( IOException e )
			{
				System.out.println("Error loading fxml");
			}
		}
	}

	
	// create new window of the form
	@FXML
	protected void handleAddRoomAction( ActionEvent event )
	{
		Parent root;
		try
		{
			root = FXMLLoader.load( getClass().getClassLoader().getResource( "view/fxml/addRoomView.fxml" ) );
			Stage stage = new Stage();
			stage.setTitle( "Add Room" );
			stage.setScene( new Scene( root, 740, 400 ) );
			stage.show();
		}
		catch( IOException e )
		{
			System.out.println("Error loading fxml!");
		}
		refresh();
	}

	
	// exprot to file
	@FXML
	protected void handleExport( ActionEvent event )
	{
		
		Stage stage = new Stage();
		// create a Directory chooser
		DirectoryChooser dir_chooser = new DirectoryChooser();

		File file = dir_chooser.showDialog( stage );

		if( file != null )
		{
			try
			{
				// write to file
				model.fileIO.HandleReadWrite.writeToFile( file.getAbsolutePath() );
				
				// show successful
				Alert alert = new Alert( Alert.AlertType.INFORMATION );
				alert.setTitle( "Done" );
				alert.setHeaderText( "File Write Successful" );
				alert.setContentText( "Find export_data.txt in folder : " + file.getAbsolutePath() );
				alert.showAndWait();

			}
			catch( IOException e )
			{
				// show error
				Alert error = new Alert( Alert.AlertType.ERROR );
				error.setTitle( "Error Occured" );
				error.setHeaderText( "Problem Writing File" );
				error.setContentText( e.getMessage() );
				error.showAndWait();
			}
		}
	}

	
	// Load from file
	@FXML
	protected void handleImport( ActionEvent event )
	{
		Stage stage = new Stage();
		// create a Directory chooser
		FileChooser dir_chooser = new FileChooser();

		File file = dir_chooser.showOpenDialog( stage );

		if( file != null )
		{
			try
			{
				// read from file
				model.fileIO.HandleReadWrite.readFromFile( file );

				Alert alert = new Alert( Alert.AlertType.INFORMATION );
				alert.setTitle( "Done" );
				alert.setHeaderText( "File Read Successful" );
				alert.setContentText( "Read file at : " + file.getAbsolutePath() );
				alert.showAndWait();

			}
			catch( MyException e )
			{
				// show errors
				Alert error = new Alert( Alert.AlertType.ERROR );
				error.setTitle( "Error Occured" );
				error.setHeaderText( "Problem reading from file" );
				error.setContentText( e.getMessage() );
				e.printStackTrace();
				error.showAndWait();
			}
			refresh();
		}
	}

	
	// exit the program
	@FXML
	protected void handleQuitAction( ActionEvent event )
	{
		System.exit( 0 );
	}
}
