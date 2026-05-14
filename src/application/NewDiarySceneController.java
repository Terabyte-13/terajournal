package application;

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

public class NewDiarySceneController extends SceneController {
	
	@FXML TextField nameField;
	@FXML PasswordField passwordField;
	@FXML PasswordField confirmPasswordField;
	@FXML Button directoryPickerButton;
	@FXML TextField pathField;
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/NewDiary.fxml"));
	DirectoryChooser dc = new DirectoryChooser();
	Hasher hasher = new Hasher();
	HasherBean hb = new HasherBean();
	Logger logger = Logger.getLogger("NewDiarySceneController");

	String key;
	

	void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
		mp.setFF(ff);
	}
	
	void setFF(FileFacade newff) {
		ff = newff;
		logger.log(Level.FINE, "FileFacade impostato: {0}", ff);
	}
	
	public void toStart() {
		sm.toStart();
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
		sm.createDiary(nameField.getText(), pathField.getText(), passwordField.getText(), confirmPasswordField.getText());
	}
	
}
