package application;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class StartSceneController extends SceneController {
	
	@FXML ComboBox<String> diaryPicker;
	
	MetadataParser mp = new MetadataParser();
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/Start.fxml"));
	void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
		
		diaryPicker.getItems().add("Importa un diario esistente");
		populateDiaryList();
	}
	
	public void toNewDiary(ActionEvent event) {
		NewDiarySceneController n = new NewDiarySceneController();
		n.loadScene(currentStage);
	}
	
	void toPasswordPrompt(String diaryPath) {
		PasswordPromptSceneController p = new PasswordPromptSceneController();
		p.diaryPath = diaryPath;
		p.loadScene(currentStage);
	}
	
	
	void populateDiaryList() {
		List<String> diaries = mp.getFieldNames("diaryList");
		for(int i = 0; i < diaries.size(); i++) {
			diaryPicker.getItems().add(diaries.get(i));
		}
	}
	
	//prendo da diaryList il filepath del diario selezionato
	public void onDiaryPickerPress(ActionEvent event) {
		String selection = diaryPicker.getValue();
		
		Path p = Paths.get(mp.getField(selection, "diaryList"));
		System.out.printf("path: %s, cartella contenitore: %s\n", p, p.getParent());
		
		toPasswordPrompt(p.toString());
		

	}
	

	
}
