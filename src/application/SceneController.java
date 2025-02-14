package application;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public abstract class SceneController {

	//gerarchia: root > scene > stage
	Stage currentStage;
	Scene scene;
	Parent root;
	
	void showScene(Stage stage, FXMLLoader sceneLoader) {
		try{
			//inizializzazione interfaccia: prepara variabili, carica CSS e carica la scena Main ---------------------
			Scene scene = new Scene(sceneLoader.load());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); //TODO CSS cambiabile, in base al tema scelto
			stage.setScene(scene);
			//titolo e icona -------------------------------------------------
			stage.setTitle("Palle Top"); //TODO titolo in base al documento aperto
			//Image icon = new Image("file:./src/resources/images/icon.png"); 
			//stage.getIcons().add(icon);
			stage.show();
			
		}catch(IOException e){
			System.out.println("IOException nello SceneController");
			e.printStackTrace();
		}
	}
	
	SceneController() { //costruttore. parte quando viene istanziata la classe
		System.out.println("nuova istanza creata: " + this);
	}
	
}
