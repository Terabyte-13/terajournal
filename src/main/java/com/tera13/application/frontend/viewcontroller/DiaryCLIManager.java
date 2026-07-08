package com.tera13.application.frontend.viewcontroller;

import com.tera13.application.frontend.view.cli.*;
import javafx.stage.Stage;

import static com.tera13.application.frontend.view.cli.Colors.*;

public class DiaryCLIManager extends DiaryUIManager {

    ViewCLI currentView;

    public void initAndStart(Stage primaryStage){
        toLogin();
    }

    public void loadScene(){
        currentView.setSm(this);
        currentView.show();
    }

    public void toLogin(){
        currentView = new UserLoginViewCLI();
        loadScene();
    }

    public void toNewDiary(){
        currentView = new NewDiaryViewCLI();
        loadScene();
    }

    public void toPasswordPrompt(String storedHash){
        currentView = new PasswordPromptViewCLI();
        loadScene();
    }

    public void toCalendar(){
        currentView = new CalendarViewCLI();
        loadScene();
    }

    public void toStart(){
        currentView = new StartViewCLI();
        loadScene();
    }

    public void toEditor(int year, int month, int day){
        currentView = new EditorViewCLI(year, month, day);
        loadScene();
    }

    public void toError(Exception e){
        System.out.println(ANSI_RED_BG + ANSI_BLACK + "  Errore!  " + ANSI_RESET);
        System.out.println(ANSI_WHITE_BG + ANSI_BLACK + e.getClass().getSimpleName() + ANSI_RESET);
        System.out.println(ANSI_WHITE_BG + ANSI_BLACK + e.getMessage() + ANSI_RESET);
        System.exit(0);
    }

}
