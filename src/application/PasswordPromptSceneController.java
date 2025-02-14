package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class PasswordPromptSceneController extends SceneController {
	//commento prova
	String diaryPath;
	MetadataParser mp = new MetadataParser();
	Hasher hasher = new Hasher();
	
	@FXML PasswordField passwordField; 
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/PasswordPrompt.fxml"));
	void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
		
		if(mp.getField("pwdHash", diaryPath).equals("notFound")) { //se non c'è password
			CalendarSceneController c = new CalendarSceneController();
			c.diaryPath = diaryPath;
			c.loadScene(currentStage);
		}
	}
	
	public void toStart() {
		StartSceneController n = new StartSceneController();
		n.loadScene(currentStage);
	}
	
	public void submitPassword() {
		String password = passwordField.getText();
		String hash = hasher.getHash(password, "SHA-256");
		String pwdHash = mp.getField("pwdHash", diaryPath);
		
		if(hash.equals(pwdHash)) {
			//NB: gli accessi a diaryList devono essere senza key. crea un ff nuovo
			CalendarSceneController c = new CalendarSceneController();
			c.diaryPath = diaryPath;
			if(!password.equals("")) {c.ffc.key = hasher.getHash(password, "MD5");} //un hash diverso come key, non quello immagazzinato
			c.loadScene(currentStage);
		}else {
			passwordField.setText("");
			passwordField.setPromptText("Password errata!");
		}

	}
	
}
