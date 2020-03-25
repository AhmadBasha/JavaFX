package view;
//Ahmed Basha A Altowairqi
//S3644668

import java.sql.SQLException;

import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.classes.CityLodge;
import model.database.DB_access;

public class Main extends Application
{
	// create city lodge
	public static CityLodge cityLodge1;
	
	// save controller
	public static MainController mainCtrl;


	@Override
	public void start( Stage stage )
	{

		try
		{
			// loading fxml which is the interface of the application 
			FXMLLoader loader = new FXMLLoader( getClass().getClassLoader().getResource( "view/fxml/mainView.fxml" ) );
			VBox root = loader.load();
            // here for the maincontroller.
			mainCtrl = loader.getController();

			Scene scene = new Scene( root, 1280, 800 );

			stage.setTitle( "City Lodge" );
			stage.setScene( scene );
			stage.show();
		}

		catch( Exception e )
		{
			e.printStackTrace();
		}
	}


	public static void main( String[] args )
	{
		// here we can access to the all room which is on the memory or on the model pagckge. 
		cityLodge1 = new CityLodge();
		try
		{
			DB_access.getRooms();
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}
		launch( args );
	}
}
