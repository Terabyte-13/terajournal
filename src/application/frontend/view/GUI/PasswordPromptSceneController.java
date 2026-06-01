package application.frontend.view.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class PasswordPromptSceneController extends SceneController {
	
	Logger logger = Logger.getLogger("PasswordPromptSC");
	
	@FXML PasswordField passwordField;

	String storedHash = "";
	
	public static final String BEAN_ERROR = "Errore nell'impostazione di un bean."; 
	
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/PasswordPrompt.fxml"));
	public void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
	}

	public void setStoredHash(String s){
		storedHash = s;
	}

	public void toStart() {sm.toStart();}
	
	public void submitPassword() {
		String password = passwordField.getText(); //password inserita
		Boolean match = sm.checkPassword(password);
		if(Boolean.TRUE.equals(match)){ //se la password inserita è corretta
			String k = sm.generateKey(password);
			sm.setKey(k); //imposto la key di DF a quella ottenuta
			sm.toCalendar();
		}else {
			passwordField.setText("");
			passwordField.setPromptText("Password errata!");
		}
	}
	
}
