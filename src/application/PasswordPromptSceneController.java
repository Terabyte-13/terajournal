package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class PasswordPromptSceneController extends SceneController {
	//commento prova
	String diaryPath;
	MetadataParser mp = new MetadataParser();
	MetadataBean mb = new MetadataBean();
	String beanAnswer;
	Hasher hasher = new Hasher();
	
	FileFacade ff;
	
	@FXML PasswordField passwordField; 
	
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/PasswordPrompt.fxml"));
	void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
		
		mp.ff = ff;
		
		//impacchetto bean e recupero risposta
		mb.setFieldName("pwdHash"); //cerco il field pwdHash nel file nel path
		mb.setPath(diaryPath);
		beanAnswer = mp.getFieldBean(mb).getFieldData();
		//-------------------
		if(beanAnswer.equals("notFound")) { //se non c'è password, apro direttamente il calendario
			CalendarSceneController c = new CalendarSceneController();
			c.diaryPath = diaryPath;
			c.setFF(ff);
			c.loadScene(currentStage);
		}
	}
	
	void setFF(FileFacade newff) {
		ff = newff;
		System.out.printf("<PasswordPromptSC> FF impostato: %s.%n", ff);
	}
	
	public void toStart() {
		StartSceneController n = new StartSceneController();
		n.loadScene(currentStage);
	}
	
	public void submitPassword() {
		String password = passwordField.getText(); //password inserita
		String hash = hasher.getHash(password, "SHA-256"); //hash della password inserita
		mb.setFieldName("pwdHash");
		mb.setPath(diaryPath);
		String pwdHash = mp.getFieldBean(mb).getFieldData(); //hash preso dal file, da confrontare a hash
		
		if(hash.equals(pwdHash)) { //se gli hash combaciano, la password inserita e' corretta
			CalendarSceneController c = new CalendarSceneController();
			c.diaryPath = diaryPath;
			if(!password.equals("")) {c.setKey(hasher.getHash(password, "MD5"));} //un hash diverso come key, non quello immagazzinato
			c.setFF(ff);
			c.loadScene(currentStage);
		}else {
			passwordField.setText("");
			passwordField.setPromptText("Password errata!");
		}

	}
	
}
