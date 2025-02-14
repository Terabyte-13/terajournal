package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

public class EditorSceneController extends SceneController {
	
	String filePath;
	String diaryPath;
	FileFacade ffc;
	
	@FXML HTMLEditor editorText;
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/Editor.fxml"));
	void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
		
		editorText.setHtmlText(ffc.loadAndDecrypt(filePath));
	}
	
	public void savePage() {
		String data = editorText.getHtmlText();
		ffc.encryptAndSave(data, filePath, false);
		toCalendar();
	}
	
	public void toCalendar(){
		CalendarSceneController c = new CalendarSceneController();
		c.diaryPath = diaryPath;
		c.ffc = ffc;
		c.loadScene(currentStage);
	}
}
