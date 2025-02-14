package application;

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
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CalendarSceneController extends SceneController {
	
	@FXML ComboBox<String> diaryPicker;
	@FXML GridPane calendarGrid;
	@FXML Label monthYearDisplay;
	
	LocalDate date = LocalDate.now();
	int day = date.getDayOfMonth();
	int month = date.getMonthValue();
	int year = date.getYear();
	
	String diaryPath;
	FileFacade ffc = new FileFacade();
	MetadataParser mp = new MetadataParser();
	
	
	EventHandler<ActionEvent> calendarButtonHandler = new EventHandler<ActionEvent>() {
		public void handle(ActionEvent event) {
			onCalendarButtonPress(event);
		}
	};
	

	
	//--------------------
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/fxml/Calendar.fxml"));
	void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua
		
		diaryPicker.getItems().add("Importa un diario esistente");
		populateDiaryList();
		diaryPicker.setValue(mp.getField("name", diaryPath));
		
		updateCalendar();
	}

	void populateDiaryList() {
		List<String> diaries = mp.getFieldNames("diaryList");
		for(int i = 0; i < diaries.size(); i++) {
			diaryPicker.getItems().add(diaries.get(i));
		}
	}
	
	void updateCalendar() {
		//aggiungi tasti giorno dinamicamente --------------------------
		int daysInMonth = LocalDate.of(year, month, day).lengthOfMonth();
		
		calendarGrid.getChildren().clear();
		for(int i = 0; i < daysInMonth; i++) {
			Button button = new Button(Integer.toString(i+1));
			if(i+1 == LocalDate.now().getDayOfMonth() && month == LocalDate.now().getMonth().getValue() && year == LocalDate.now().getYear()) {
				button.setId("calendarButtonToday");
			//}else if(fm.getPageAmount(diaryPath, year, month, i+1) >= 1) {
			//	button.setId("calendarButtonHasFile"); //TODO temporaneo
			}else button.setId("calendarButton");
			
			button.setOnAction(calendarButtonHandler);
			button.setUserData(i+1); //per passare il numero del tasto all'handler
			calendarGrid.add(button, i%7, i/7);
		}
		monthYearDisplay.setText(Integer.toString(month) + "/" + Integer.toString(year));
	}
	
	//------------------
	
	public void onCalendarButtonPress(ActionEvent event) {
		Node caller = (Node)event.getSource();
		int selectedDay = (int)caller.getUserData();
		System.out.println(selectedDay);
		
		EditorSceneController e = new EditorSceneController();
		e.filePath = mp.getField("folder", diaryPath) + "/" + year + "/" + month + "/" + selectedDay + ".html";
		e.diaryPath = diaryPath;
		e.ffc = ffc;
		e.loadScene(currentStage);
	}
	
	//prendo da diaryList il filepath del diario selezionato
	public void onDiaryPickerPress(ActionEvent event) { //viene chiamato due volte per qualche motivo
		/*String selection = diaryPicker.getValue();
		
		Path p = Paths.get(mp.getField(selection, "diaryList"));
		System.out.printf("path: %s, cartella contenitore: %s\n", p, p.getParent());
		
		diaryPath = mp.getField(selection, "diaryList");
		loadScene(currentStage);*/
	}
	
	public void toStart() {
		StartSceneController n = new StartSceneController();
		n.loadScene(currentStage);
	}
}
