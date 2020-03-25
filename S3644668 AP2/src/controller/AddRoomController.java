package controller;

import java.sql.SQLException;
import java.time.LocalDate;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.exceptions.DuplicateIdException;
import model.helper.DateTime;
import view.Main;

public class AddRoomController
{
	// here all the varaiables that need to be handling from the view. 
	public ToggleGroup roomType;

	public Label startIdLabel;
	public TextField idText;

	public Label numRoomsLabel;
	public Slider numSlider;

	public TextArea summaryText;

	public DatePicker lastMainDatePicker;

	public TextField imageFileText;

	public Button addBtn;


	// form handler
	@FXML
	protected void handleAddRoomAction( ActionEvent event )
	{
		Alert alertWarning = null;
		
		// something is empty
		if( idText.getText().trim().isEmpty() || summaryText.getText().trim().isEmpty() || imageFileText.getText().trim().isEmpty()
				|| (startIdLabel.getText().charAt( 0 ) == 'S' && lastMainDatePicker.getValue() == null) )
		{
			alertWarning = new Alert( Alert.AlertType.ERROR );
			alertWarning.setTitle( "Failed!" );
			alertWarning.setHeaderText( "Insert room operation failed" );
			alertWarning.setContentText( "Error: Please complete the form!" );
			alertWarning.showAndWait();
			return;
		}
		// get the room number
		int intId = 0;
		try
		{
			intId = Integer.parseInt( idText.getText().trim() );
		}
		catch( NumberFormatException e )
		{
			alertWarning = new Alert( Alert.AlertType.ERROR );
			alertWarning.setTitle( "Failed!" );
			alertWarning.setHeaderText( "Insert room operation failed" );
			alertWarning.setContentText( "Error: room Id should be a number!!" );
			alertWarning.showAndWait();
			return;
		}
		
		// get other details
		String id = startIdLabel.getText() + intId;
		
		if( id.length() > 7 )
		{
			alertWarning = new Alert( Alert.AlertType.ERROR );
			alertWarning.setTitle( "Failed!" );
			alertWarning.setHeaderText( "Insert room operation failed" );
			alertWarning.setContentText( "Error: Room number should be lesser than 1,000,000." );
			alertWarning.showAndWait();
			return;
		}

		int numRooms = Integer.parseInt( numRoomsLabel.getText() );
		
		String summary = summaryText.getText();
		if( summary.length() > 20)
		{
			summary = summary.substring( 20 );
		}

		String image = imageFileText.getText();

		DateTime lastMainDate = null;
		
		// get date if it is a suite
		if( id.charAt( 0 ) == 'S' )
		{
			LocalDate temp = lastMainDatePicker.getValue();
			lastMainDate = new DateTime( temp.getDayOfMonth(), temp.getMonthValue(), temp.getYear() );
		}

		try
		{
			// try to add into memory and db
			Main.cityLodge1.addRoom( id, summary, numRooms, lastMainDate, image );
		}
		catch( DuplicateIdException | SQLException e )
		{
			// show error
			alertWarning = new Alert( Alert.AlertType.ERROR );
			alertWarning.setTitle( "Failed!" );
			alertWarning.setHeaderText( "Insert room operation failed" );
			alertWarning.setContentText( "Error: " + e.getMessage() );
			alertWarning.showAndWait();
			return;
		}
		
		// show complete
		alertWarning = new Alert( Alert.AlertType.INFORMATION );
		alertWarning.setTitle( "Successful" );
		alertWarning.setHeaderText( "Insert room Successful" );
		alertWarning.setContentText( " Room ID: " + id );
		alertWarning.showAndWait();
		
		// get the window and close it.
		Stage s = (Stage) addBtn.getScene().getWindow();
		s.close();
		
		
		// refresh the main window
		Main.mainCtrl.refresh();

	}

	// function called automatically on creation of window
	public void initialize()
	{
		// toggle logic to make changes to the window. &&
		roomType.selectedToggleProperty().addListener( (ChangeListener<Toggle>) ( ov, old_toggle, new_toggle )->
		{
			RadioButton rb = (RadioButton) roomType.getSelectedToggle();
			if( rb != null )
			{
				startIdLabel.setText( rb.getId() );
				if( rb.getId().charAt( 0 ) == 'S' )
				{
					// disable number slider
					numSlider.setDisable( true );
					
					// enable date picker
					lastMainDatePicker.setDisable( false );
					
					// set value of slider to 6
					numSlider.setValue( 6.0 );
					
					// set lable of numbers to 6
					numRoomsLabel.setText( String.format( "%d", 6 ) );

				}
				else
				{
					// opposite of above.
					numSlider.setDisable( false );
					lastMainDatePicker.setDisable( true );
					numSlider.setValue( 1.0 );
					numRoomsLabel.setText( String.format( "%d", 1 ) );
				}
			}
		} );
		
		// slider logic to change window on change of toggle
		numSlider.valueProperty().addListener( (ChangeListener<Number>) ( observable, oldValue, newValue )->
		{

			if( !numSlider.isValueChanging() || newValue.doubleValue() == numSlider.getMax()
					|| newValue.doubleValue() == numSlider.getMin() )
			{
				// ignore values 3 and 5
				if( newValue.intValue() == 3 || newValue.intValue() == 5 )
				{
					numSlider.increment();
					newValue = newValue.intValue() + 1;
				}
				
				// set value to 6 if disabled by toggle
				if( numSlider.isDisabled() )
				{
					numSlider.setValue( 6.0 );
					newValue = 6;
				}
				
				// set toggle to suite if slider set to 6
				if( newValue.intValue() == 6 )
				{
					roomType.selectToggle( roomType.getToggles().get( 1 ) );
				}

				numRoomsLabel.setText( String.format( "%d", newValue.intValue() ) );
			}
		} );
	}
}
