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
	MetadataParser mp = new MetadataParser();
	Hasher hasher = new Hasher();
	StringBean hb = new StringBean();
	
	FileFacade ff; //passato dalla scena precedente
	String key;
	

	void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
		mp.setFF(ff);
	}
	
	void setFF(FileFacade newff) {
		ff = newff;
		System.out.printf("<PasswordPromptSC> FF impostato: %s.%n", ff);
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
		if(nameField.getText().equals("") || pathField.getText().equals("")) {
			System.out.println("Ci sono dei campi vuoti.");
			return;
		}
		if(!passwordField.getText().equals(confirmPasswordField.getText())) {
			System.out.println("password e confirmPassword non combaciano.");
			return;
		}
		
		String metadataFilePath = pathField.getText() + "/" + nameField.getText() + "/" + nameField.getText() + ".jm";
		
		//impacchettamento fileBean per creare directory e file metadati ------
		FileBean fb = new FileBean();
		fb.setPath(metadataFilePath);
		fb.setKey(null);
		fb.setData("");
		//--------------------------------
		
		//metadataBean per modificare il file metadati ------
		MetadataBean mb = new MetadataBean();
		//--------------------------------
		
		if(ff.encryptAndSaveBean(fb, false, false) == 1) { //creo directory e file metadati per il diario
			System.out.println("Diario creato.");
			
			//aggiungo il diario alla lista dei diari
			System.out.println("aggiungo il diario alla lista dei diari");
			mb.setPath("diaryList");
			mb.setFieldName(nameField.getText());
			mb.setFieldData(metadataFilePath);
			mp.setFieldBean(mb);
			
			//adesso opero sul file metadati del diario
			mb.setPath(metadataFilePath);
			
			//riempio metadati
			mb.setFieldName("name");
			mb.setFieldData(nameField.getText());
			mp.setFieldBean(mb);
			
			mb.setFieldName("folder");
			mb.setFieldData(pathField.getText() + "/" + nameField.getText());
			mp.setFieldBean(mb);
			
			if(passwordField.getText().equals("")) { //Se non inserisco una password, il diario non avrà password.
				mb.setFieldName("pwdHash");
				mb.setFieldData("");
				mp.setFieldBean(mb);
			} else { //Se viene inserita una password, salvo l'hash
				mb.setFieldName("pwdHash");
				mb.setFieldData(hasher.getHash(passwordField.getText(), "SHA-256"));
				mp.setFieldBean(mb);
				}
			
			//se e' tutto andato a buon fine, inizializzo e apro il calendario
			CalendarSceneController c = new CalendarSceneController();
			c.diaryPath = mp.getField(nameField.getText(), "diaryList");
			if(!passwordField.getText().equals("")) {
				
				key = hasher.getHash(passwordField.getText(), "MD5");
				} //uso l'hash MD5 come key per decifrare. l'altro hash serve a farti entrare
			c.setFF(ff);
			c.setKey(key);
			c.loadScene(currentStage);
			
		}else {System.out.println("Diario NON creato.");}
	}
	
}
