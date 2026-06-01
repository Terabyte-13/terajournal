package application.frontend.view.GUI;

import java.util.List;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class StartSceneController extends SceneController {
	
	@FXML ComboBox<String> diaryPicker;
	
	Logger logger = Logger.getLogger("StartSceneController");

	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/Start.fxml"));

	public void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse TODO ?? rivedi sto commento
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
		
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
	
	public void onImportButtonPress(ActionEvent event) {
		
	}
	

	
}
