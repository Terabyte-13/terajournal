package application.frontend.view.CLI;

import java.util.List;

import static application.frontend.view.CLI.Colors.*;

public class StartViewCLI extends ViewCLI {

    Selector s = new Selector();

    public void show(){
        while(true) {
            System.out.println(ANSI_CYAN_BG + ANSI_BLACK + "   t e r a j o u r n a l   " + ANSI_RESET);
            System.out.println(ANSI_CYAN + "Benvenuto/a! Scegli un'opzione:" + ANSI_RESET);

            int choice = s.getChoice("Apri un diario esistente", "Crea un nuovo diario", "Esci");
            System.out.println("Input: " + choice);

            if(choice == 1) {
                diaryPicker();
            }else if (choice == 2) {
                sm.toNewDiary();
            }else if (choice == 3) {
                System.out.println(ANSI_CYAN + "Arrivederci!" + ANSI_RESET);
                System.exit(0);
            }else {
                System.out.println(ANSI_YELLOW + "Opzione invalida!" + ANSI_RESET);
            }
        }
    }

    void diaryPicker(){
        System.out.println(ANSI_CYAN_BG + ANSI_BLACK + "   Seleziona il diario...   " + ANSI_RESET);

        List<String> diaries = sm.getDiaryNames(); //TODO beanizza?
        int choice = s.getChoice(diaries.toArray(new String[0])); //mando la lista di stringhe come argomenti

        String d = diaries.get(choice-1);
        System.out.println(ANSI_YELLOW + "Apro il diario: " + d +  ANSI_RESET);
        sm.openDiary(d);
    }

}


