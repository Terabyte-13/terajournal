package com.tera13.application.frontend.view.cli;

import static com.tera13.application.frontend.view.cli.Colors.*;
import static com.tera13.application.frontend.view.cli.Colors.ANSI_RESET;

public class UserLoginViewCLI extends ViewCLI {

    private static final Selector s = new Selector();
    private Boolean stay = true;

    public void show(){
        while(Boolean.TRUE.equals(stay)) {
            System.out.println(ANSI_CYAN_BG + ANSI_BLACK + " Accedi " + ANSI_RESET);

            String username = s.getString(true, 1, 64, "il nome utente");
            String password = s.getString(true, 1, 1000, "la password per l'utente");

            Boolean success = getSm().userLogin(username, password);

            if(Boolean.FALSE.equals(success)){
                System.out.println(ANSI_RED + " Credenziali errate! " + ANSI_RESET);
            } else {
                System.out.println(ANSI_GREEN + " Credenziali corrette! " + ANSI_RESET);
                stay = false;
                getSm().toStart();
            }
        }
    }
}
