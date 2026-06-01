package application.frontend.viewcontroller;

import application.frontend.view.CLI.*;
import javafx.stage.Stage;

public class CLIManager extends UIManager{

    ViewCLI currentView;

    public void initAndStart(Stage primaryStage) throws Exception{
        toStart();
    }

    public void loadScene(){
        currentView.sm = this;
        currentView.show();
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

}
