package com.tera13.application.frontend.view.gui;

import com.tera13.application.frontend.viewcontroller.DiaryGUIManager;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class ErrorPopupGUI {

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    ButtonType quit = new ButtonType("Esci");

    public void show(Exception e){
        alert.setTitle("Errore!");
        alert.setHeaderText(e.getClass().getSimpleName());
        alert.setContentText(e.getMessage());
        alert.getButtonTypes().setAll(quit);
        Optional<ButtonType> choice = alert.showAndWait();
        if(choice.isPresent() && choice.get() == quit) {
            System.exit(0);
        } else System.exit(0);
    }

}
