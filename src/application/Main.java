package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	FXMLLoader Splash = new FXMLLoader(getClass().getResource("/fxml/Splash.fxml"));
	StartSceneController Start = new StartSceneController();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = Splash.load();
			Scene scene = new Scene(root,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.centerOnScreen();
			
			Start.loadScene(primaryStage);
			
			} catch(Exception e) {
				e.printStackTrace();
			}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
