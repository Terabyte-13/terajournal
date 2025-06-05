package application;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CalendarSceneController extends SceneController {
	
	@FXML ComboBox<String> diaryPicker;
	@FXML GridPane calendarGrid;
	@FXML Label monthYearDisplay;
	@FXML DatePicker datePicker;

	
	LocalDate date = LocalDate.now();
	int day = date.getDayOfMonth();
	int month = date.getMonthValue();
	int year = date.getYear();
	
	String diaryPath;
	String diaryFolder;
	FileFacade ff;
	String key;
	
	MetadataParser mp = new MetadataParser();
	MetadataBean mb = new MetadataBean();
	
	CalendarSceneController(){
		String caller = Thread.currentThread().getStackTrace()[2].getClassName();
		System.out.printf("<CalendarSC> aperto da %s.%n", caller);
	}
	
	//l'evento da associare ai tasti del calendario generati da updateCalendar()
	//espressione lambda. "event" è l'evento innescatore. verrà eseguito il codice dopo "->"
	EventHandler<ActionEvent> calendarButtonHandler = event -> onCalendarButtonPress(event);
	
	//--------------------
	
	void setKey(String k) {
		key = k;
		System.out.println("chiave : " + key);
	}
	
	void setFF(FileFacade newff) {
		ff = newff;
		System.out.printf("<CalendarSC> FF impostato: %s.%n", ff);
	}
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/Calendar.fxml"));
	void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
		mp.setFF(ff);
		
		mb.setPath(diaryPath); //opero sul file metadati del diario
		
		mb.setFieldName("folder"); //cerco il field "folder"
		diaryFolder = mp.getFieldBean(mb).getFieldData(); //prendo i dati del field restituiti sul bean da mp
		
		populateDiaryList();
		
		mb.setFieldName("name"); //cerco il field "name"
		diaryPicker.setValue(mp.getFieldBean(mb).getFieldData()); //prendo i dati del field restituiti sul bean da mp
		
		updateCalendar();
	}
	
	//non va
	void populateDiaryList() {
		List<String> diaries = mp.getFieldNames("diaryList");
		for(int i = 0; i < diaries.size(); i++) {
			//diaryPicker.getItems().add(diaries.get(i));
		}
	}

	//aggiunge programmaticamente i tasti dei giorni del calendario
	void updateCalendar() {
		int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
		
		calendarGrid.getChildren().clear();
		for(int i = 1; i <= daysInMonth; i++) {
			Button button = new Button(Integer.toString(i));	
			//Evidenzio il giorno attuale
			if(i == LocalDate.now().getDayOfMonth() && month == LocalDate.now().getMonth().getValue() && year == LocalDate.now().getYear()) {
				button.setId("calendarButtonToday");
			//Evidenzio i giorni con un file associato
			}else if(ff.checkForFile(diaryFolder + File.separator + year + File.separator + month + File.separator + i + ".html") == true) {
				button.setId("calendarButtonHasFile");
			}else button.setId("calendarButton");
			
			button.setOnAction(calendarButtonHandler);
			button.setUserData(i); //per passare il numero del tasto all'handler
			calendarGrid.add(button, (i-1)%7, (i-1)/7);
		}
		monthYearDisplay.setText(month + File.separator + year);
	}
	
	// funzioni per FXML ----------------------------------------------------------------------------------------------
	
	public void nextMonth(ActionEvent event) {
		if(month < 12){ month++;}
		else {
			month = 1;
			year++;
		}
		updateCalendar();
	}
	
	public void prevMonth(ActionEvent event) {
		if(month > 1){ month--;}
		else {
			month = 12;
			year--;
		}
		updateCalendar();
	}
	
	//apertura della pagina di un giorno
	public void onCalendarButtonPress(ActionEvent event) {
		Node caller = (Node)event.getSource();
		int selectedDay = (int)caller.getUserData();
		System.out.println(selectedDay);
		
		EditorSceneController e = new EditorSceneController();
		e.filePath = diaryFolder + File.separator + year + File.separator + month + File.separator + selectedDay + ".html";
		e.diaryPath = diaryPath;
		e.setFF(ff);
		e.setKey(key);
		e.loadScene(currentStage);
	}
	
	//prendo da diaryList il filepath del diario selezionato
	public void onDiaryPickerPress(ActionEvent event) { //viene chiamato subito al partire della scena per qualche motivo
		/*System.out.println("AGAGAGAGA");
		String selection = diaryPicker.getValue();
		
		Path p = Paths.get(mp.getField(selection, "diaryList"));
		System.out.printf("path: %s, cartella contenitore: %s\n", p, p.getParent());
		
		toPasswordPrompt(p.toString());*/
	}
	
	public void jumpToDate(ActionEvent event) {
		LocalDate input = datePicker.getValue();
		if(input != null) {
			year = input.getYear();
			month = input.getMonthValue();
			updateCalendar();
		}
	}
	
	public void jumpToToday() {
		year = date.getYear();
		month = date.getMonthValue();
		updateCalendar();
	}
	

	
	public void toStart() {
		StartSceneController n = new StartSceneController();
		n.loadScene(currentStage);
	}
	
	void toPasswordPrompt(String diaryPath) {
		PasswordPromptSceneController p = new PasswordPromptSceneController();
		p.diaryPath = diaryPath;
		p.setFF(ff);
		p.loadScene(currentStage);
	}
	
}


