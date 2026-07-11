package com.tera13.application.frontend.viewcontroller;

import com.tera13.application.backend.DiaryFacade;
import com.tera13.application.backend.UserLogin.LoginFacade;
import com.tera13.application.bean.PageBean;
import com.tera13.application.bean.*;
import com.tera13.application.exception.CreateDiaryException;
import com.tera13.application.exception.FileFacadeException;
import com.tera13.application.exception.MetadataParserException;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    Controller grafico per lo use case "leggi/scrivi pagina di diario",
    mediatore tra le View e diaryFacade.
    Contiene funzioni generali del diario che ogni view può chiamare,
    e funzioni per prendere/passare il controllo alle varie View.
*/

public abstract class DiaryUIManager {

    LoginFacade lf = new LoginFacade();
    DiaryFacade df = new DiaryFacade();

    String currentKey = "";
    String currentDiaryPath = ""; //path del file metadati
    String currentDiaryFolder; //path della cartella

    Logger logger = Logger.getLogger("DiaryUIManager");

    public static final String BEAN_ERROR = "Errore nell'impostazione di un bean.";

    public abstract void initAndStart(Stage primaryStage) throws IOException;

    public abstract void loadScene();

    public abstract void toLogin();

    public abstract void toNewDiary();

    public abstract void toPasswordPrompt(String storedHash);

    public abstract void toCalendar();

    public abstract void toStart();

    public abstract void toEditor(int year, int month, int day);

    public abstract void toError(Exception e);

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

        try {
            cd.setName(name);
            cd.setPath(path);
            cd.setPassword(password);
            cd.setConfirmPassword(confirmPassword);
            df.createDiaryBean(cd);
        } catch (CreateDiaryException e) {
            toError(e);
        }
        openDiary(name); //i dati inseriti come arogmenti sono già stati controllati dal CreateDiaryBean quindi li do per buoni e li uso
    }

    //prepara il bean da mandare a DiaryFacade.savePage
    public void savePage(String data, int year, int month, int day) {
        //se il file è vuoto, non salvo
        if(!data.equals("<html dir=\"ltr\"><head></head><body contenteditable=\"true\"></body></html>")) {
            //impacchettamento bean --------
            PageBean fb = new PageBean();
            try {
                fb.setData(data);
                String p = currentDiaryFolder + File.separator + year + File.separator + month + File.separator + day + ".html";
                fb.setPath(p);
                fb.setKey(currentKey);
            }catch(IllegalArgumentException e) {
                logger.log(Level.SEVERE, BEAN_ERROR);
                e.printStackTrace();
            }
            try {
                df.savePage(fb);
            } catch (FileFacadeException e) {
                toError(e);
            }
            //------------------------------
        }
        else {logger.log(Level.INFO, "Non c'è nulla da salvare");}
    }

    public String loadPage(int year, int month, int day) {
        PageBean fb = new PageBean();
        fb.setPath(currentDiaryFolder + File.separator + year + File.separator + month + File.separator + day + ".html");
        fb.setKey(currentKey);

        try {
            fb = df.loadPageBean(fb);
        } catch (FileFacadeException e) {
            toError(e);
        }

        return fb.getData();
    }

    //Crea il file diaryList se non c'è
    public void checkForDiaryList(){
        try {
            df.checkForDiaryList();
        } catch (FileFacadeException e) {
            toError(e);
        }
    }

    //Restituisce i nomi dei diari in diaryList
    public List<String> getDiaryNames(){
        try{
            return df.getDiaryNames().getNames();
        } catch (MetadataParserException e) {
            toError(e);
            return Collections.emptyList();
        }
    }

    public String getCurrentDiaryName(){
        String fileName = Paths.get(currentDiaryPath).getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');

        // Se c'è un punto e non è all'inizio del file (es. file nascosti tipo .gitignore)
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }

        return fileName;
    }

    public Boolean checkPassword(String password){
        PasswordBean pb = new PasswordBean();
        try {
            pb.setPassword(password);
        } catch (IllegalArgumentException e) {
            toError(e);
        }
        FilePathBean fp = new FilePathBean();
        fp.setPath(currentDiaryPath);
        try {
            return df.checkPasswordBean(pb, fp);
        } catch (MetadataParserException e) {
            toError(e);
            return false;
        }
    }

    public String generateKey(String password){
        PasswordBean pb = new PasswordBean();
        String key = "";
        try {
            pb.setPassword(password);
            pb = df.generateKeyBean(pb);
            key = pb.getKey();
            if(key == null) toError(new Exception("Errore nella generazione della chiave!"));
        } catch (IllegalArgumentException e) {
            toError(e);
        }
        return key;
    }

    //cambia il currentDiary, richiede inserimento password se necessario, va al calendario
    public void openDiary(String diaryName){
        DiaryBean d = new DiaryBean();
        try {
            d.setName(diaryName);
            d = df.getDiaryMetadata(d);
        } catch (MetadataParserException e) {
            toError(e);
        }
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
        } else {currentDiaryFolder = "";}

        currentKey = key;
    }

    public void setKey(String key) {
        currentKey = key;
    }

    //restituisce true se il giorno specificato ha una pagina di diario scritta
    public Boolean isPageWritten(int year, int month, int day){
        DateBean db = new DateBean();
        try{
            db.setYear(year);
            db.setMonth(month);
            db.setDay(day);
        }catch(IllegalArgumentException e){
            toError(e);
        }
        FilePathBean fp = new FilePathBean();
        fp.setPath(currentDiaryFolder);
        return df.isPageWrittenBean(db, fp);
    }

    public Boolean userLogin(String username, String password){
        LoginBean lb = new LoginBean();
        lb.setUsername(username);
        lb.setPassword(password);
        lb = lf.userLogin(lb);

        String p = lb.getDiaryListPath();
        if(!p.equals("notFound")){
            df.setDiaryList(lb);
            return true;
        }

        return false;
    }

}
