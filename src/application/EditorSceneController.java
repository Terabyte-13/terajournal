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
	String key;
	
	Logger logger = Logger.getLogger("EditorSceneController");
	public static final String BEAN_ERROR = "Errore nell'impostazione di un bean.";
	
	@FXML HTMLEditor editorText;
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/Editor.fxml"));
	void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		logger.log(Level.INFO, "Carico l'editor...");
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
		
		//carico i dati dal file sull'editor
		//caricamento dati nel bean ---------
		FileBean fb = new FileBean();
		try {
			fb.setPath(filePath);
			fb.setKey(key);
			fb = ff.loadAndDecryptBean(fb, true);
		}catch(IllegalArgumentException e) {
			logger.log(Level.SEVERE, BEAN_ERROR);
			e.printStackTrace();
		}
		//------------------------------
		logger.log(Level.INFO, "Carico il file...");
		editorText.setHtmlText(fb.getData());
	}
	
	void setKey(String k) {
		key = k;
	}
	
	void setFF(FileFacade newff) {
		ff = newff;
	}
	
	//salva i dati dall'editor sul file e torna al calendario
	public void savePage() {
		sm.savePage(editorText.getHtmlText(), filePath, key);
		toCalendar();
	}
	
	public void toCalendar(){
		sm.toCalendar(diaryPath, key);
	}
}
