package application.frontend.view.cli;

import static application.frontend.view.cli.Colors.*;
import static application.frontend.view.cli.Colors.ANSI_RESET;
import static application.frontend.view.cli.Colors.ANSI_YELLOW;

public class PasswordPromptViewCLI extends ViewCLI {

    Selector s = new Selector();
    Boolean stay = true;

    public void show(){
        while(stay) {
            System.out.println(ANSI_CYAN_BG + ANSI_BLACK + " Il diario è protetto da password " + ANSI_RESET);

            String p = s.getString(false, 0, 0, "la password per il diario");
            Boolean match = sm.checkPassword(p);
            if(Boolean.TRUE.equals(match)){ //se la password inserita è corretta
                String k = sm.generateKey(p);
                sm.setKey(k); //imposto la key di DF a quella ottenuta
                System.out.println(ANSI_YELLOW + "Password corretta!" + ANSI_RESET);
                stay = false;
                sm.toCalendar();
            }else {
                System.out.println(ANSI_YELLOW + "Password errata!" + ANSI_RESET);
            }
        }
    }
}
