package com.tera13.application.frontend.view.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserLoginViewGUI extends ViewGUI {

    @FXML TextField nameField;
    @FXML PasswordField passwordField;

    FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("/main/resources/fxml/UserLogin.fxml"));
    public void loadScene(Stage stage) {
        sceneLoader.setController(this); //per far usare l'istanza che ho creato nel codice, altrimenti se ne crea una nuova
        showScene(stage, sceneLoader);
        currentStage = stage;
    }

    public void submitLogin(){
        String username = nameField.getText();
        String password = passwordField.getText();
        Boolean success = getSm().userLogin(username, password);

        if(Boolean.FALSE.equals(success)){
            nameField.setText("");
            nameField.setPromptText("Credenziali errate!");
            passwordField.setText("");
            passwordField.setPromptText("Credenziali errate!");
        } else {
            getSm().toStart();
        }

    }

}
