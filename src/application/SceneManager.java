package application;

import application.bean.DiaryBean;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    gestore delle scene, funge da classe centrale che prende/passa il controllo ai vari SceneController
*/

public class SceneManager {

    public static final String BEAN_ERROR = "Errore nell'impostazione di un bean.";

    //stage della finestra principale
    Stage currentStage;
    /*mantenendo un unico currentSceneController che mantiene il riferimento a solo la scene attuale, teoricamente
    gli scenecontroller precedenti, una volta rimosso il riferimento, vengono rimossi dal garbage collector*/
    SceneController currentSceneController;

    DiaryFacade df = new DiaryFacade();

    Logger logger = Logger.getLogger("SceneManager");

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
        toStart();
    }

    void loadScene(){
        currentSceneController.sm = this; //passo un riferimento in modo che la scena può dire a questa classe di caricarne un'altra prima di returnare
        currentSceneController.loadScene(currentStage);
    }

    void toNewDiary() {
        currentSceneController = new NewDiarySceneController();
        loadScene();
    }

    void toPasswordPrompt(String storedHash){
        PasswordPromptSceneController p = new PasswordPromptSceneController();
        currentSceneController = p;
        p.setStoredHash(storedHash);
        loadScene();
    }

    void toCalendar(){
        CalendarSceneController c = new CalendarSceneController();
        currentSceneController = c;
        loadScene();
    }

    void toStart(){
        switchDiary("", "");//per sicurezza
        currentSceneController = new StartSceneController();
        loadScene();
    }

    void toEditor(int year, int month, int day){
        EditorSceneController e = new EditorSceneController();
        //DiaryBean d = df.getDiaryMetadata("");
        //e.filePath = diaryFolder + File.separator + year + File.separator + month + File.separator + selectedDay + ".html";
        //e.diaryPath = ;
        e.setDate(year, month, day);
        currentSceneController = e;
        loadScene();
    }


    //creazione di un nuovo diario
    public void createDiary(String name, String path, String password, String confirmPassword) {
        df.createDiary(name, path, password, confirmPassword); //TODO beanizza
        toCalendar();
    }

    //salvataggio pagina diario
    public void savePage(String data, int year, int month, int day) {
        //se il file è vuoto, non salvo
        if(!data.equals("<html dir=\"ltr\"><head></head><body contenteditable=\"true\"></body></html>")) {
            //impacchettamento bean --------
            FileBean fb = new FileBean();
            try {
                fb.setData(data);
                //in p metto il pezzo che conosce SceneManager, il path del diario verrà inserito da DiaryFacade
                String p = File.separator + year + File.separator + month + File.separator + day + ".html";
                fb.setPath(p);
            }catch(IllegalArgumentException e) {
                logger.log(Level.SEVERE, BEAN_ERROR);
                e.printStackTrace();
            }
            df.savePage(fb);
            //------------------------------
        }
        else {logger.log(Level.INFO, "Non c'è nulla da salvare");}
    }

    String loadPage(int year, int month, int day){
        //TODO beanizza la data
        FileBean fb = df.loadPage(year, month, day);
        return fb.getData();
    }

    //Crea il file diaryList se non c'è
    void checkForDiaryList(){
        df.checkForDiaryList();
    }

    //Restituisce i nomi dei diari in diaryList
    List<String> getDiaryNames(){
        return df.getDiaryNames(); //TODO beanizza?
    }

    String getCurrentDiaryName(){
        return df.getDiaryMetadata("").getName();
    }

    Boolean checkPassword(String password){
        return df.checkPassword(password); //TODO beanizza
    }

    String generateKey(String password){
        return df.generateKey(password); //TODO beanizza
    }

    //in comune tra più scene. potrebbe rimanere qui
    //cambia il currentDiary, richiede inserimento password se necessario, va al calendario
    void openDiary(String diaryName){
        DiaryBean d = df.getDiaryMetadata(diaryName);
        String storedHash = d.getPwdHash();
        switchDiary(diaryName, ""); //imposto il diario, ma non ho ancora la key
        if(storedHash.equals("notFound")){ //se non c'è pwd, vai direttamente al calendario, senza key
            toCalendar();
        } else { //se c'è pwd, vai al password prompt per inserire la pwd e generare una key
            toPasswordPrompt(storedHash);
        }
    }

    void switchDiary(String diaryName, String key){
        df.switchDiary(diaryName, key); //TODO DiaryBean?
    }

    void setKey(String key) {
        df.setKey(key); //TODO DiaryBean?
    }

    //per Calendar, potrebbe andare in un controller tutto suo
    //restituisce la lista di giorni che hanno pagina, per un dato mese
    Boolean isPageWritten(int year, int month, int day){
        return df.isPageWritten(year, month, day); //TODO Beanizza..?
    }

}
