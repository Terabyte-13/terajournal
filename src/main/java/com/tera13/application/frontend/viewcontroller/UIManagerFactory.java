package com.tera13.application.frontend.viewcontroller;

public class UIManagerFactory {

    public UIManager getUiManager(boolean useCli){
        if(Boolean.FALSE.equals(useCli)) {
            return new GUIManager();
        }else {
            return new CLIManager();
        }
    }

}
