package com.tera13.application.frontend.view.cli;

import static com.tera13.application.frontend.view.cli.Colors.*;
import static com.tera13.application.frontend.view.cli.Colors.ANSI_CYAN;
import static com.tera13.application.frontend.view.cli.Colors.ANSI_RESET;

public class CalendarViewCLI extends ViewCLI {

    Selector s = new Selector();

    public void show(){
        System.out.println(ANSI_CYAN_BG + ANSI_BLACK + " Diario: " + getSm().getCurrentDiaryName() + " " + ANSI_RESET);
        System.out.println(ANSI_CYAN + "Scegli una pagina da aprire." + ANSI_RESET);
        int[] date = s.getDate("data pagina");
        int year = date[0];
        int month = date[1];
        int day = date[2];
        getSm().toEditor(year, month, day);
    }
}


