package application;
	
import javafx.application.Application;
import javafx.stage.Stage;

/*RICORDA: se il programma non parte perchè javafx non trova la variabile d'ambiente DISPLAY
esegui "xhost +si:localuser:$(whoami)" nel terminale*/

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
