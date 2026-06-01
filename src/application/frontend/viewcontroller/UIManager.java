package application.frontend.viewcontroller;

//TODO leva sto commento: nel diagramma metti questa astratta come quadratino accanto a GUIManager che la implementa

import application.DiaryFacade;
import application.FileBean;
import application.bean.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    gestore delle scene, funge da controller grafico centrale che prende/passa
    il controllo alle varie View e da mediatore tra le View e diaryFacade
*/

public abstract class UIManager {

    DiaryFacade df = new DiaryFacade();

    String currentKey = "";
    String currentDiaryPath = ""; //path del file metadati TODO cambia nome
    String currentDiaryFolder; //path della cartella

    Logger logger;
    public static final String BEAN_ERROR = "Errore nell'impostazione di un bean.";

    abstract public void initAndStart(Stage primaryStage) throws Exception;

    abstract public void loadScene();

    abstract public void toNewDiary();

    abstract public void toPasswordPrompt(String storedHash);

    abstract public void toCalendar();

    abstract public void toStart();

    abstract public void toEditor(int year, int month, int day);


    //creazione di un nuovo diario
    public void createDiary(String name, String path, String password, String confirmPassword) {
        //controllo qua per dare subito un messaggio. Se qualcosa va proprio storto, gli stessi controlli sono presenti anche in CreateDiaryBean
        if(name.equals("") || path.equals("")) {
            logger.log(Level.INFO, "Ci sono dei campi vuoti.");
            return;
        }
        if(!password.equals(confirmPassword)) {
            logger.log(Level.INFO, "password e confirmPassword non combaciano.");
            return;
        }

        CreateDiaryBean cd = new CreateDiaryBean();
        cd.setName(name);
        cd.setPath(path);
        cd.setPassword(password);
        cd.setConfirmPassword(confirmPassword);

        String k = df.createDiaryBean(cd).getKey();
        openDiary(name); //i dati inseriti come arogmenti sono già stati controllati dal CreateDiaryBean quindi li do per buoni e li uso
    }

    //prepara il bean da mandare a DiaryFacade.savePage
    public void savePage(String data, int year, int month, int day) {
        //se il file è vuoto, non salvo
        if(!data.equals("<html dir=\"ltr\"><head></head><body contenteditable=\"true\"></body></html>")) {
            //impacchettamento bean --------
            FileBean fb = new FileBean();
            try {
                fb.setData(data);
                String p = currentDiaryFolder + File.separator + year + File.separator + month + File.separator + day + ".html";
                fb.setPath(p);
                fb.setKey(currentKey);
            }catch(IllegalArgumentException e) {
                logger.log(Level.SEVERE, BEAN_ERROR);
                e.printStackTrace();
            }
            df.savePage(fb);
            //------------------------------
        }
        else {logger.log(Level.INFO, "Non c'è nulla da salvare");}
    }

    public String loadPage(int year, int month, int day) throws Exception {
        FileBean fb = new FileBean();
        fb.setPath(currentDiaryFolder + File.separator + year + File.separator + month + File.separator + day + ".html");
        fb.setKey(currentKey);
        fb = df.loadPageBean(fb);
        return fb.getData();
    }

    //Crea il file diaryList se non c'è
    public void checkForDiaryList(){
        df.checkForDiaryList();
    }

    //Restituisce i nomi dei diari in diaryList
    public List<String> getDiaryNames(){
        return df.getDiaryNames(); //TODO beanizza?
    }

    public String getCurrentDiaryName(){
        return Paths.get(currentDiaryPath).getFileName().toString();
    }

    public Boolean checkPassword(String password){
        PasswordBean pb = new PasswordBean();
        pb.setPassword(password);
        FilePathBean fp = new FilePathBean();
        fp.setPath(currentDiaryPath);
        return df.checkPasswordBean(pb, fp);
    }

    public String generateKey(String password){
        PasswordBean pb = new PasswordBean();
        pb.setPassword(password);
        return df.generateKeyBean(pb);
    }

    //cambia il currentDiary, richiede inserimento password se necessario, va al calendario
    public void openDiary(String diaryName){
        DiaryBean d = df.getDiaryMetadata(diaryName);
        String storedHash = d.getPwdHash();
        switchDiary(d.getName(),d.getFolder(),""); //imposto il diario, ma non ho ancora la key
        if(storedHash.equals("notFound")){ //se non c'è pwd, vai direttamente al calendario, senza key
            toCalendar();
        } else { //se c'è pwd, vai al password prompt per inserire la pwd e generare una key
            toPasswordPrompt(storedHash);
        }
    }

    public void switchDiary(String diaryName, String diaryPath, String key){
        if(diaryPath != null){
            currentDiaryPath = diaryPath + File.separator + diaryName + ".jm";
            currentDiaryFolder = diaryPath;
        } else {currentDiaryFolder = "";} //TODO vedi sta cosa

        currentKey = key;
    }

    public void setKey(String key) {
        currentKey = key;
    }

    //restituisce la lista di giorni che hanno pagina, per un dato mese
    public Boolean isPageWritten(int year, int month, int day){
        DateBean db = new DateBean();
        db.setYear(year);
        db.setMonth(month);
        db.setDay(day);
        FilePathBean fp = new FilePathBean();
        fp.setPath(currentDiaryFolder);
        return df.isPageWrittenBean(db, fp);
    }
}
