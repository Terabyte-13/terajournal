package com.tera13.application.frontend.view.gui;

import java.io.File;
import java.time.LocalDate;

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
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalendarViewGUI extends ViewGUI {
	
	@FXML ComboBox<String> diaryPicker;
	@FXML GridPane calendarGrid;
	@FXML Label monthYearDisplay;
	@FXML DatePicker datePicker;

	
	LocalDate date = LocalDate.now();
	int month = date.getMonthValue();
	int year = date.getYear();

	Logger logger = Logger.getLogger("FileManagerDemo");
	
	public CalendarViewGUI(){
		String caller = Thread.currentThread().getStackTrace()[2].getClassName();
		logger.log(Level.FINE, "Aperto da {0}", caller);
	}
	
	//l'evento da associare ai tasti del calendario generati da updateCalendar()
	//espressione lambda. "event" è l'evento innescatore. verrà eseguito il codice dopo "->"
	EventHandler<ActionEvent> calendarButtonHandler = event -> onCalendarButtonPress(event);
	
	//--------------------
	
	FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/main/resources/fxml/Calendar.fxml"));
	public void loadScene(Stage stage) { //per passare la variabile sceneLoader alla superclasse
		sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
		showScene(stage, sceneLoader);
		currentStage = stage; //immagazzino lo stage passato dalla scena precedente, per poterlo utilizzare qua

		//populateDiaryList();

		diaryPicker.setValue(sm.getCurrentDiaryName()); //imposto sull'UI il nome del diario attuale
		
		updateCalendar();
	}
	
	//non va TODO df.getDiaryList
	void populateDiaryList() {
		//List<String> diaries = sm.mp.getFieldNames("diaryList");
		//for(int i = 0; i < diaries.size(); i++) {
			//diaryPicker .getItems().add(diaries.get(i));
		//}
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
			}else if(Boolean.TRUE.equals(sm.isPageWritten(year, month, i))) {
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
		int selectedDay = (int)caller.getUserData(); //serve per capire il numero del giorno del tasto premuto

		//String fp = diaryFolder + File.separator + year + File.separator + month + File.separator + selectedDay + ".html";
		sm.toEditor(year, month, selectedDay);
	}
	
	//prendo da diaryList il filepath del diario selezionato TODO leva?
	public void onDiaryPickerPress(ActionEvent event) { //viene chiamato subito al partire della scena per qualche motivo
		/*
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
		sm.toStart();
	}
	
	void toPasswordPrompt(String diaryPath) {
		sm.toPasswordPrompt(diaryPath);
	}
	
}


