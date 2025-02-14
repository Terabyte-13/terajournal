package application;

import java.io.File;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class NewDiarySceneController extends SceneController {
	
	@FXML TextField nameField;
	@FXML PasswordField passwordField;
	@FXML PasswordField confirmPasswordField;
	@FXML Button directoryPickerButton;
	@FXML TextField pathField;
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/NewDiary.fxml"));
	DirectoryChooser dc = new DirectoryChooser();
	FileFacade ff = new FileFacade();
	MetadataParser mp = new MetadataParser();
	Hasher hasher = new Hasher();
	
	void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
	}
	
	public void toStart() {
		StartSceneController n = new StartSceneController();
		n.loadScene(currentStage);
	}
	
	public void pickDirectory(){
		File selection = dc.showDialog(currentStage);
		if(selection == null) {
			System.out.println("nessun file scelto.");
		}else {
			pathField.setText(selection.getAbsolutePath());
		}
	}
	
	public void createDiary() {	
		if(nameField.getText() == null || pathField.getText() == null) {
			System.out.println("Ci sono dei campi vuoti.");
			return;
		}
		if(!passwordField.getText().equals(confirmPasswordField.getText())) {
			System.out.println("password e confirmPassword non combaciano.");
			return;
		}
		
		String metadataFilePath = pathField.getText() + "/" + nameField.getText() + "/" + nameField.getText() + ".jm";
		if(ff.encryptAndSave("", metadataFilePath, false) == 1) {
			System.out.println("Diario creato.");
			mp.setField(nameField.getText(), "diaryList", metadataFilePath);
			
			//riempi metadati
			mp.setField("name", metadataFilePath, nameField.getText());
			mp.setField("folder", metadataFilePath, pathField.getText() + "/" + nameField.getText());
			if(passwordField.getText().equals("")) {
				mp.setField("pwdHash", metadataFilePath, "");
			} else {mp.setField("pwdHash", metadataFilePath, hasher.getHash(passwordField.getText(), "SHA-256"));}
			
			CalendarSceneController c = new CalendarSceneController();
			c.diaryPath = mp.getField(nameField.getText(), "diaryList");
			if(!passwordField.getText().equals("")) {c.ffc.key = hasher.getHash(passwordField.getText(), "MD5");}
			c.loadScene(currentStage);
			
		}else {System.out.println("Diario NON creato.");}
	}
	
}
