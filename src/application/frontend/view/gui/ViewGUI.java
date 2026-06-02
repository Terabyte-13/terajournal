package application.frontend.view.gui;

import java.io.IOException;

import application.frontend.viewcontroller.GUIManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class ViewGUI {

	//gerarchia: root > scene > stage
	Stage currentStage;
	Scene scene;
	Parent root;
	Logger l = Logger.getLogger("SceneController");
	public GUIManager sm;

	void setSm(GUIManager GUIManager){
		sm = GUIManager;
	}

	void showScene(Stage stage, FXMLLoader sceneLoader) {
		try{
			//inizializzazione interfaccia: prepara variabili, carica CSS e carica la scena Main ---------------------
			scene = new Scene(sceneLoader.load());
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); //TODO posso levarlo? il css se lo carica da solo
			stage.setScene(scene);
			//titolo e icona -------------------------------------------------
			stage.setTitle("terajournal"); //TODO titolo in base al documento aperto
			Image icon = new Image("file:./src/resources/images/icon.png"); 
			stage.getIcons().add(icon);
			stage.show();
			
		}catch(IOException e){
			l.log(Level.SEVERE, "IOException nello SceneController");
			e.printStackTrace();
		}
	}

	public abstract void loadScene(Stage stage);
	
	ViewGUI() {
		String caller = Thread.currentThread().getStackTrace()[2].getClassName(); //TODO leva
		l.log(Level.FINE, "Nuova istanza creata da {0}: {1}", new Object[]{caller, this});
	}
	
}
