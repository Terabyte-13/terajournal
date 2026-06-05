package com.tera13.application.frontend.viewcontroller;

import com.tera13.application.frontend.view.gui.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class GUIManager extends UIManager {

    //stage della finestra principale
    Stage currentStage;
    /*mantenendo un unico currentView che mantiene il riferimento a solo la scene attuale, teoricamente
    gli scenecontroller precedenti, una volta rimosso il riferimento, vengono rimossi dal garbage collector*/
    ViewGUI currentView;

    public void initAndStart(Stage primaryStage) throws IOException {

        FXMLLoader splash = new FXMLLoader(getClass().getResource("/main/resources/fxml/Splash.fxml"));
        Parent root = splash.load();
        Scene scene = new Scene(root,800,600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Terajournal");
        primaryStage.show();
        primaryStage.centerOnScreen();

        currentStage = primaryStage;

        toStart();
    }

    public void loadScene(){
        currentView.setSm(this); //passo un riferimento in modo che la view può comunicare con questa classe
        currentView.loadScene(currentStage);
    }

    public void toNewDiary() {
        currentView = new NewDiaryViewGUI();
        loadScene();
    }

    public void toPasswordPrompt(String storedHash){
        PasswordPromptViewGUI p = new PasswordPromptViewGUI();
        currentView = p;
        p.setStoredHash(storedHash);
        loadScene();
    }

    public void toCalendar(){
        CalendarViewGUI c = new CalendarViewGUI();
        currentView = c;
        loadScene();
    }

    public void toStart(){
        switchDiary("", "", "");//per sicurezza
        currentView = new StartViewGUI();
        loadScene();
    }

    public void toEditor(int year, int month, int day){
        EditorViewGUI e = new EditorViewGUI();
        e.setDate(year, month, day);
        currentView = e;
        loadScene();
    }

    public void toError(Exception e){
        ErrorPopupGUI ep = new ErrorPopupGUI();
        ep.setSm(this);
        ep.show(e);
    }

}
