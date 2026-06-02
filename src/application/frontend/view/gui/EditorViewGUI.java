package application.frontend.view.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EditorViewGUI extends ViewGUI {

	int year;
	int month;
	int day;
	
	Logger logger = Logger.getLogger("EditorSceneController");
	public static final String BEAN_ERROR = "Errore nell'impostazione di un bean.";
	
	@FXML HTMLEditor editorText;
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/Editor.fxml"));
	public void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		logger.log(Level.INFO, "Carico l'editor...");
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua

		logger.log(Level.INFO, "Carico il file...");
		loadPage();
	}

	public void setDate(int y, int m, int d){
		year = y;
		month = m;
		day = d;
	}

	public void loadPage(){
		try {
			editorText.setHtmlText(sm.loadPage(year, month, day));
		} catch (Exception e) {
			editorText.setHtmlText("");
		}
	}

	//salva i dati dall'editor sul file e torna al calendario
	public void savePage() {
		sm.savePage(editorText.getHtmlText(), year, month, day);
		toCalendar();
	}
	
	public void toCalendar(){
		sm.toCalendar();
	}
}
