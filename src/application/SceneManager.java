package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    gestore delle scene, funge da classe centrale che prende/passa il controllo ai vari SceneController
*/
//TODO tieni tutti i riferimenti alle robe qua, le altre classi le chiameranno dal riferimento a questa classe

public class SceneManager {

    public static final String BEAN_ERROR = "Errore nell'impostazione di un bean.";

    FileFacade ff = new FileFacade(); //questo fileFacade deve essere l'unica istanza di fileFacade, passata tra le classi, altrimenti in modalità demo verranno creati più DB separati
    //stage della finestra principale
    Stage currentStage;
    /*mantenendo un unico currentSceneController che mantiene il riferimento a solo la scene attuale, teoricamente
    gli scenecontroller precedenti vengono chiusi dal garbage collector*/
    SceneController currentSceneController;
    MetadataParser mp = new MetadataParser();
    Hasher hasher = new Hasher();

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


    //creazione di un nuovo diario
    public void createDiary(String name, String path, String password, String confirmPassword) {
        try {
            if(name.equals("") || path.equals("")) {
                logger.log(Level.INFO, "Ci sono dei campi vuoti.");
                return;
            }
            if(!password.equals(confirmPassword)) {
                logger.log(Level.INFO, "password e confirmPassword non combaciano.");
                return;
            }

            String metadataFilePath = path + File.separator + name + File.separator + name + ".jm";

            //impacchettamento fileBean per creare directory e file metadati ------
            FileBean fb = new FileBean();
            fb.setPath(metadataFilePath);
            fb.setKey(null);
            fb.setData("");
            //--------------------------------

            MetadataBean mb = new MetadataBean();
            HasherBean hb = new HasherBean();

            if(ff.encryptAndSaveBean(fb, false, false) == 1) { //creo directory e file metadati per il diario
                logger.log(Level.INFO, "Diario creato.");

                //aggiungo il diario alla lista dei diari
                mb.setPath("diaryList");
                mb.setFieldName(name);
                mb.setFieldData(metadataFilePath);
                mp.setFieldBean(mb);
                logger.log(Level.INFO, "Diario aggiunto alla lista dei diari");

                //adesso opero sul file metadati del diario
                mb.setPath(metadataFilePath);

                //riempio metadati
                mb.setFieldName("name");
                mb.setFieldData(name);
                mp.setFieldBean(mb);

                mb.setFieldName("folder");
                mb.setFieldData(path + File.separator + name);
                mp.setFieldBean(mb);

                if(password.equals("")) { //Se non inserisco una password, il diario non avrà password.
                    mb.setFieldName("pwdHash");
                    mb.setFieldData("");
                    mp.setFieldBean(mb);
                } else { //Se viene inserita una password, salvo l'hash
                    mb.setFieldName("pwdHash");
                    hb.setString(password);
                    hb.setAlgorithm("SHA-256");
                    mb.setFieldData(hasher.getHashBean(hb).getString());
                    mp.setFieldBean(mb);
                }

                //se e' tutto andato a buon fine, inizializzo e apro il calendario
                String d = mp.getField(name, "diaryList"); //TODO beanizza
                String key = "";
                if(!password.equals("")) {
                    hb.setString(password);
                    hb.setAlgorithm("MD5");
                    key = hasher.getHashBean(hb).getString();
                } //uso l'hash MD5 come key per decifrare. l'altro hash serve a farti entrare
                toCalendar(d, key); //apro subito il diaro appena creato

            }else {logger.log(Level.INFO, "Diario NON creato.");}
        }catch(IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Errore nell'impostazione di un bean");
            e.printStackTrace();
        }

    }

    public void savePage(String data, String path, String key) {
        //se il file è vuoto, non salvo
        if(!data.equals("<html dir=\"ltr\"><head></head><body contenteditable=\"true\"></body></html>")) {
            //impacchettamento bean --------
            FileBean fb = new FileBean();
            try {
                fb.setData(data);
                fb.setPath(path);
                fb.setKey(key);
            }catch(IllegalArgumentException e) {
                logger.log(Level.SEVERE, BEAN_ERROR);
                e.printStackTrace();
            }
            ff.encryptAndSaveBean(fb, false, true);
            //------------------------------
        }
        else {logger.log(Level.INFO, "Non c'è nulla da salvare");}
    }
}
