package com.tera13.application.frontend.view.cli;

import java.util.List;

import static com.tera13.application.frontend.view.cli.Colors.*;

public class StartViewCLI extends ViewCLI {

    Selector s = new Selector();
    Boolean stay = true;

    public void show(){
        while(Boolean.TRUE.equals(stay)) {
            System.out.println(ANSI_CYAN_BG + ANSI_BLACK + "   t e r a j o u r n a l   " + ANSI_RESET);
            System.out.println(ANSI_CYAN + "Benvenuto/a! Scegli un'opzione:" + ANSI_RESET);

            int choice = s.getChoice("Apri un diario esistente", "Crea un nuovo diario", "Esci");
            System.out.println("Input: " + choice);

            switch (choice) {
                case 1 -> diaryPicker();
                case 2 -> getSm().toNewDiary();
                case 3 -> {
                    System.out.println(ANSI_CYAN + "Arrivederci!" + ANSI_RESET);
                    stay = false;
                    System.exit(0);
                }
                default -> System.out.println(ANSI_YELLOW + "Opzione invalida!" + ANSI_RESET);
            }
        }
    }

    void diaryPicker(){
        System.out.println(ANSI_CYAN_BG + ANSI_BLACK + "   Seleziona il diario...   " + ANSI_RESET);

        List<String> diaries = getSm().getDiaryNames();
        int choice = s.getChoice(diaries.toArray(new String[0])); //mando la lista di stringhe come argomenti

        String d = diaries.get(choice-1);
        System.out.println(ANSI_YELLOW + "Apro il diario: " + d +  ANSI_RESET);
        getSm().openDiary(d);
    }

}


