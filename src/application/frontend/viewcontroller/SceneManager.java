package application.frontend.viewcontroller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import application.frontend.view.GUI.SceneController;
import application.frontend.view.GUI.StartSceneController;
import application.frontend.view.GUI.NewDiarySceneController;
import application.frontend.view.GUI.PasswordPromptSceneController;
import application.frontend.view.GUI.CalendarSceneController;
import application.frontend.view.GUI.EditorSceneController;



public class SceneManager extends UIManager {

    public static final String BEAN_ERROR = "Errore nell'impostazione di un bean.";

    //stage della finestra principale
    Stage currentStage;
    /*mantenendo un unico currentSceneController che mantiene il riferimento a solo la scene attuale, teoricamente
    gli scenecontroller precedenti, una volta rimosso il riferimento, vengono rimossi dal garbage collector*/
    SceneController currentSceneController; //TODO rinomina

    public void initAndStart(Stage primaryStage) throws Exception{

        FXMLLoader splash = new FXMLLoader(getClass().getResource("/fxml/Splash.fxml"));
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
        currentSceneController.sm = this; //passo un riferimento in modo che la view può comunicare con questa classe
        currentSceneController.loadScene(currentStage);
    }

    public void toNewDiary() {
        currentSceneController = new NewDiarySceneController();
        loadScene();
    }

    public void toPasswordPrompt(String storedHash){
        PasswordPromptSceneController p = new PasswordPromptSceneController();
        currentSceneController = p;
        p.setStoredHash(storedHash);
        loadScene();
    }

    public void toCalendar(){
        CalendarSceneController c = new CalendarSceneController();
        currentSceneController = c;
        loadScene();
    }

    public void toStart(){
        switchDiary("", "", "");//per sicurezza
        currentSceneController = new StartSceneController();
        loadScene();
    }

    public void toEditor(int year, int month, int day){
        EditorSceneController e = new EditorSceneController();
        e.setDate(year, month, day);
        currentSceneController = e;
        loadScene();
    }

}
