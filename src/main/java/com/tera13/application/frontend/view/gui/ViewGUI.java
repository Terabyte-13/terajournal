package com.tera13.application.frontend.view.gui;

import java.io.IOException;

import com.tera13.application.frontend.viewcontroller.GUIManager;
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
	private GUIManager sm;

	void showScene(Stage stage, FXMLLoader sceneLoader) {
		try{
			//inizializzazione interfaccia: prepara variabili, carica CSS e carica la scena Main ---------------------
			scene = new Scene(sceneLoader.load());
			stage.setScene(scene);
			//titolo e icona -------------------------------------------------
			stage.setTitle("terajournal");
			Image icon = new Image("file:./src/main.resources/images/icon.png");
			stage.getIcons().add(icon);
			stage.show();
			
		}catch(IOException e){
			l.log(Level.SEVERE, "IOException nello SceneController");
			e.printStackTrace();
		}
	}

	public abstract void loadScene(Stage stage);
	
	ViewGUI() {
		String caller = Thread.currentThread().getStackTrace()[2].getClassName();
		l.log(Level.FINE, "Nuova istanza creata da {0}: {1}", new Object[]{caller, this});
	}

	public GUIManager getSm() {
		return sm;
	}

	public void setSm(GUIManager sm) {
		this.sm = sm;
	}
}
