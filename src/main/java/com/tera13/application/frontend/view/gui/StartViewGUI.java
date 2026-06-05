package com.tera13.application.frontend.view.gui;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class StartViewGUI extends ViewGUI {
	
	@FXML ComboBox<String> diaryPicker;

	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/main/resources/fxml/Start.fxml"));

	public void loadScene(Stage stage) {
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage;
		
		//se non e' gia' presente, crea il file diaryList ------
		sm.checkForDiaryList();
		populateDiaryList();
	}
	
	public void toNewDiary(ActionEvent event) {
		sm.toNewDiary();
	}
	
	
	void populateDiaryList() {
		List<String> diaries = sm.getDiaryNames();
		if(diaries.isEmpty()){return;}
		for(int i = 0; i < diaries.size(); i++) {
			diaryPicker.getItems().add(diaries.get(i));
		}
	}
	
	//prendo da diaryList il filepath del diario selezionato
	public void onDiaryPickerPress(ActionEvent event) {
		String selection = diaryPicker.getValue();
		sm.openDiary(selection);
	}
	

	
}
