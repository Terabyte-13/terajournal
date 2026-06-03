package com.tera13.application.frontend.view.gui;

import com.tera13.application.frontend.viewcontroller.GUIManager;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class ErrorPopupGUI {
    GUIManager sm;
    public void setSm(GUIManager GUIManager){
        sm = GUIManager;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    ButtonType goBack = new ButtonType("Torna alla schermata iniziale");
    ButtonType quit = new ButtonType("Esci");

    public void show(Exception e){
        alert.setTitle("Errore!");
        alert.setHeaderText(e.getClass().getSimpleName());
        alert.setContentText(e.getMessage());
        alert.getButtonTypes().setAll(goBack,quit);
        Optional<ButtonType> choice = alert.showAndWait();
        if(choice.isPresent() && choice.get() == goBack) {
            sm.toStart();
        }else if(choice.isPresent() && choice.get() == quit){
            System.exit(0);
        }else System.exit(0);
    }

}
