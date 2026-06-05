package com.tera13.application.frontend.view.gui;

import java.io.File;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewDiaryViewGUI extends ViewGUI {
	
	@FXML TextField nameField;
	@FXML PasswordField passwordField;
	@FXML PasswordField confirmPasswordField;
	@FXML Button directoryPickerButton;
	@FXML TextField pathField;
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/main/resources/fxml/NewDiary.fxml"));
	DirectoryChooser dc = new DirectoryChooser();
	Logger logger = Logger.getLogger("NewDiarySceneController");
	

	public void loadScene(Stage stage) {
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage;
	}
	
	public void toStart() {
		getSm().toStart();
	}
	
	public void pickDirectory(){
		File selection = dc.showDialog(currentStage);
		if(selection == null) {
			logger.log(Level.INFO, "Nessun file scelto.");
		}else {
			pathField.setText(selection.getAbsolutePath());
		}
	}

	public void createDiary() {
		getSm().createDiary(nameField.getText(), pathField.getText(), passwordField.getText(), confirmPasswordField.getText());
	}
	
}
