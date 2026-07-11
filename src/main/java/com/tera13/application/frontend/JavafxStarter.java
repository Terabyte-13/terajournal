package com.tera13.application.frontend;

import com.tera13.application.frontend.viewcontroller.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.getenv;

public class JavafxStarter extends Application {

    private static final String FRONTEND = getenv("APP_FRONTEND");
    private static final Logger logger = Logger.getLogger("JavafxStarter");

    public void l(String[] args){
        launch(args); //questa funzione non può essere chiamata da altre classi, quindi faccio così
    }

    @Override
    public void start(Stage primaryStage) {

        DiaryUIManager du;
        if(FRONTEND.equals("cli")){
            du = new DiaryCLIManager();
        }else{
            du = new DiaryGUIManager();
        }

        try {
            du.initAndStart(primaryStage);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException nell'inizializzazione del DiaryUIManager");
            e.printStackTrace();
        }
    }
}
