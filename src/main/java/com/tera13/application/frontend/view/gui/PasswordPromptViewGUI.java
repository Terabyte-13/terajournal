package com.tera13.application.frontend.view.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class PasswordPromptViewGUI extends ViewGUI {
	
	@FXML PasswordField passwordField;

    FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/main/resources/fxml/PasswordPrompt.fxml"));
	public void loadScene(Stage stage) {
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage;
	}

	public void toStart() {
		getSm().toStart();}
	
	public void submitPassword() {
		String password = passwordField.getText(); //password inserita
		Boolean match = getSm().checkPassword(password);
		if(Boolean.TRUE.equals(match)){ //se la password inserita è corretta
			String k = getSm().generateKey(password); //imposto la key di DF a quella ottenuta
			getSm().setKey(k);
			getSm().toCalendar();
		}else {
			passwordField.setText("");
			passwordField.setPromptText("Password errata!");
		}
	}
	
}
