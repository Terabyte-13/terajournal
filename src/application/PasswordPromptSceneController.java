package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PasswordPromptSceneController extends SceneController {
	//commento prova
	String diaryPath;
	MetadataBean mb = new MetadataBean();
	String beanAnswer;
	Hasher hasher = new Hasher();
	HasherBean hb = new HasherBean();
	
	Logger logger = Logger.getLogger("PasswordPromptSC");
	
	@FXML PasswordField passwordField; 
	
	public static final String BEAN_ERROR = "Errore nell'impostazione di un bean."; 
	
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/PasswordPrompt.fxml"));
	void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua

		try { //TODO df.getDiaryMetadata
			//impacchetto bean e recupero risposta
			mb.setFieldName("pwdHash"); //cerco il field pwdHash nel file nel path
			mb.setPath(diaryPath);
			beanAnswer = sm.mp.getFieldBean(mb).getFieldData();
			//-------------------
			if(beanAnswer.equals("notFound")) { //se non c'è password, apro direttamente il calendario
				sm.toCalendar(diaryPath, "");
			}
		}catch(IllegalArgumentException e) {
			logger.log(Level.SEVERE, BEAN_ERROR);
			e.printStackTrace();
		}
	}
	
	public void toStart() {sm.toStart();}
	
	public void submitPassword() {
		try { //TODO df.checkPassword()
			String password = passwordField.getText(); //password inserita
			hb.setString(password);
			hb.setAlgorithm("SHA-256");
		String hash = hasher.getHashBean(hb).getString(); //hash della password inserita
		mb.setFieldName("pwdHash");
		mb.setPath(diaryPath);
		String pwdHash = sm.mp.getFieldBean(mb).getFieldData(); //hash preso dal file, da confrontare a hash
		
		if(hash.equals(pwdHash)) { //se gli hash combaciano, la password inserita e' corretta
			String k = "";
			if(!password.equals("")) {
					hb.setString(password);
					hb.setAlgorithm("MD5");
				k = hasher.getHashBean(hb).getString();
				}
			sm.toCalendar(diaryPath, k);
		}else {
			passwordField.setText("");
			passwordField.setPromptText("Password errata!");
		}
		}catch(IllegalArgumentException e) {
			logger.log(Level.SEVERE, BEAN_ERROR);
			e.printStackTrace();
		}
	}
	
}
