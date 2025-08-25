package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditorSceneController extends SceneController {
	
	String filePath;
	String diaryPath;
	FileFacade ff;
	String key;
	
	Logger logger = Logger.getLogger("FileManagerDemo");
	
	@FXML HTMLEditor editorText;
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/Editor.fxml"));
	void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
		
		//carico i dati dal file sull'editor
		//caricamento dati nel bean ---------
		FileBean fb = new FileBean();
		fb.setPath(filePath);
		fb.setKey(key);
		fb = ff.loadAndDecryptBean(fb, true);
		//------------------------------
		editorText.setHtmlText(fb.getData());
	}
	
	void setKey(String k) {
		key = k;
	}
	
	void setFF(FileFacade newff) {
		ff = newff;
	}
	
	//salva i dati dall'editor sul file
	public void savePage() {
		String data = editorText.getHtmlText();
		
		//se il file è vuoto, non salvo   TODO fai che rileva quando è uguale a prima
		if(!data.equals("<html dir=\"ltr\"><head></head><body contenteditable=\"true\"></body></html>")) {	
			//impacchettamento bean --------
			FileBean fb = new FileBean();
			fb.setData(data);
			fb.setPath(filePath);
			fb.setKey(key);
			//------------------------------
			ff.encryptAndSaveBean(fb, false, true);
		}
		else {logger.log(Level.INFO, "Non c'è nulla da salvare");;}
		
		toCalendar(); //TODO porta fuori
	}
	
	public void toCalendar(){
		CalendarSceneController c = new CalendarSceneController();
		c.diaryPath = diaryPath;
		c.setFF(ff);
		c.setKey(key);
		c.loadScene(currentStage);
	}
}
