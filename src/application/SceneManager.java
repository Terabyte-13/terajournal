package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Calendar;

/*
    gestore delle scene, funge da classe centrale che prende/passa il controllo ai vari SceneController
*/

public class SceneManager {

    FileFacade ff = new FileFacade(); //questo fileFacade deve essere l'unica istanza di fileFacade, passata tra le classi, altrimenti in modalità demo verranno creati più DB separati
    //stage della finestra principale
    Stage currentStage;
    /*mantenendo un unico currentSceneController che mantiene il riferimento a solo la scene attuale, teoricamente
    gli scenecontroller precedenti vengono chiusi dal garbage collector*/
    SceneController currentSceneController;
    MetadataParser mp = new MetadataParser();

    void initAndStart(Stage primaryStage) throws Exception{
        FXMLLoader splash = new FXMLLoader(getClass().getResource("/fxml/Splash.fxml"));
        Parent root = splash.load();
        Scene scene = new Scene(root,800,600);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Terajournal");
        primaryStage.show();
        primaryStage.centerOnScreen();

        currentStage = primaryStage;
        mp.setFF(ff); //TODO bean?

        currentSceneController = new StartSceneController();
        loadScene();
    }

    void loadScene(){
        currentSceneController.setFF(ff);
        currentSceneController.sm = this; //passo un riferimento in modo che la scena può dire a questa classe di caricarne un'altra prima di returnare
        currentSceneController.mp = mp;
        currentSceneController.loadScene(currentStage);
    }

    void toNewDiary() {
        currentSceneController = new NewDiarySceneController();
        loadScene();
    }

    void toPasswordPrompt(String diaryPath){
        PasswordPromptSceneController p = new PasswordPromptSceneController();
        p.diaryPath = diaryPath;
        currentSceneController = p;
        loadScene();
    }

    void toCalendar(String diaryPath, String key){
        CalendarSceneController c = new CalendarSceneController();
        c.diaryPath = diaryPath;
        c.setKey(key);
        currentSceneController = c;
        loadScene();
    }

    void toStart(){
        currentSceneController = new StartSceneController();
        loadScene();
    }

    void toEditor(String filePath, String diaryPath, String key){
        EditorSceneController e = new EditorSceneController();
        e.filePath = filePath;
        e.diaryPath = diaryPath; //TODO usa dei setter pure per sta roba
        e.setKey(key);
        currentSceneController = e;
        loadScene();
    }

}
