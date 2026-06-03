package com.tera13.application.frontend.view.cli;

import static com.tera13.application.frontend.view.cli.Colors.*;

public class NewDiaryViewCLI extends ViewCLI{
    Selector s = new Selector();

    public void show(){

        boolean stay = true;
        String name;
        String path;
        String password = "";
        String confirmPassword = "";

        System.out.println(ANSI_CYAN_BG + ANSI_BLACK + " Crea nuovo diario " + ANSI_RESET);
        name = s.getString(true, 1, 32, "nome");
        path = s.getString(true, 1, 128, "percorso cartella");
        while(stay){
            password = s.getString(false, 0, 0, "password");
            confirmPassword = s.getString(false, 0, 0, "conferma password");
            if(password.equals(confirmPassword)) stay = false;
            else System.out.println(ANSI_YELLOW + "Password e conferma password non combaciano!" + ANSI_RESET);
        }

        sm.createDiary(name, path, password, confirmPassword);

    }
}
