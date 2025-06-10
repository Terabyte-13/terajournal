package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	
	Boolean useCLI = false;
	
	@Override
	public void start(Stage primaryStage) {
		if(useCLI == false) {	
			FXMLLoader splash = new FXMLLoader(getClass().getResource("/fxml/Splash.fxml"));
			StartSceneController start = new StartSceneController();
			try {
				Parent root = splash.load();
				Scene scene = new Scene(root,800,600);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setScene(scene);
				primaryStage.show();
				primaryStage.centerOnScreen();
				
				start.loadScene(primaryStage);
				
				} catch(Exception e) {
					e.printStackTrace();
				}
		}else {
			CLIController cc = new CLIController();
			cc.start();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
