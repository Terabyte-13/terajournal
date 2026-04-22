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
		if(Boolean.FALSE.equals(useCLI)) {
			try {
				SceneManager sm = new SceneManager();
				sm.initAndStart(primaryStage);

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
