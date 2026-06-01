package application.frontend.view.CLI;

import static application.frontend.view.CLI.Colors.*;
import static application.frontend.view.CLI.Colors.ANSI_CYAN;
import static application.frontend.view.CLI.Colors.ANSI_RESET;
import static application.frontend.view.CLI.Colors.ANSI_YELLOW;

public class CalendarViewCLI extends ViewCLI {

    Selector s = new Selector();

    public void show(){
        System.out.println(ANSI_CYAN_BG + ANSI_BLACK + " Diario: " + sm.getCurrentDiaryName() + " " + ANSI_RESET);
        System.out.println(ANSI_CYAN + "Scegli una pagina da aprire." + ANSI_RESET);
        int[] date = s.getDate("data pagina");
        int year = date[0];
        int month = date[1];
        int day = date[2];
        sm.toEditor(year, month, day);
    }
}


