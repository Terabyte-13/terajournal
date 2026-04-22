package application;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class StartSceneController extends SceneController {
	
	@FXML ComboBox<String> diaryPicker;

	MetadataBean mb = new MetadataBean();
	
	Logger logger = Logger.getLogger("StartSceneController");
	
	public static final String DIARYLIST = "diaryList";
	public static final String BEAN_ERROR = "Errore nell'impostazione di un bean."; 
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/Start.fxml"));

	void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse TODO ?? rivedi sto commento
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
		
		//se non e' gia' presente, crea il file diaryList ------
		try {
			if(Boolean.FALSE.equals(ff.checkForFile(DIARYLIST))) { //TODO bean?
				logger.log(Level.INFO, "diaryList non esiste, lo creo.");
				FileBean fb = new FileBean();
				fb.setPath(DIARYLIST);
				fb.setKey(null);
				fb.setData("");
				ff.encryptAndSaveBean(fb, false, false);
			}
		}catch(IllegalArgumentException e) {
			logger.log(Level.SEVERE, BEAN_ERROR);
			e.printStackTrace();
		}
		//--------------------------------
		
		populateDiaryList();
	}
	
	public void toNewDiary(ActionEvent event) {
		sm.toNewDiary();
	}
	
	
	void populateDiaryList() {
		List<String> diaries = mp.getFieldNames(DIARYLIST);
		if(diaries.isEmpty()){return;}
		for(int i = 0; i < diaries.size(); i++) {
			diaryPicker.getItems().add(diaries.get(i));
		}
	}
	
	//prendo da diaryList il filepath del diario selezionato
	public void onDiaryPickerPress(ActionEvent event) {
		String selection = diaryPicker.getValue();
		
		//Impacchettamento bean da mandare a getField
		try {
			mb.setFieldName(selection);
			mb.setPath(DIARYLIST);
			mb = mp.getFieldBean(mb); //faccio inserire il fieldData corrispondente, da MetadataParser
		}catch(IllegalArgumentException e) {
			logger.log(Level.SEVERE, BEAN_ERROR);
			e.printStackTrace();
		}
		//-------------------------------------------
		
		Path p = Paths.get(mb.getFieldData());
		logger.log(Level.INFO, "path: {0}, cartella contenitore: {1}", new Path[]{p, p.getParent()});
		
		sm.toPasswordPrompt(p.toString());
	}
	
	public void onImportButtonPress(ActionEvent event) {
		
	}
	

	
}
