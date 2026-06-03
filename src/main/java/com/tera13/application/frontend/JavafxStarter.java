package com.tera13.application.frontend;

import com.tera13.application.frontend.viewcontroller.UIManager;
import com.tera13.application.frontend.viewcontroller.UIManagerFactory;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavafxStarter extends Application {

    Boolean useCLI = false;
    Logger logger = Logger.getLogger("JavafxStarter");

    public void l(String[] args){
        launch(args); //questa funzione non può essere chiamata da altre classi, quindi faccio così
    }

    @Override
    public void start(Stage primaryStage) {
        UIManagerFactory uf = new UIManagerFactory();
        UIManager u = uf.getUiManager(useCLI);
        try {
            u.initAndStart(primaryStage);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException nell'inizializzazione dell'UIManager");
            e.printStackTrace();
        }
    }
}
