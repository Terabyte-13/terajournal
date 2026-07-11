package com.tera13.application.frontend.view.cli;

import java.time.LocalDate;
import java.util.Scanner;

import static com.tera13.application.frontend.view.cli.Colors.ANSI_RESET;
import static com.tera13.application.frontend.view.cli.Colors.ANSI_YELLOW;

public class Selector {

    private Scanner scanner;
    private static final String INSERT = "inserisci ";

    Selector(){
        scanner = new Scanner(System.in);
    }

    int getChoice(String... n) {
        int argc = 0;
        for(String i : n) {
            if(i.equals("")) continue; //passo alla prossima iterazione se l'opzione è vuota
            argc++;
            System.out.println(Colors.ANSI_CYAN + argc + ". " + i + ANSI_RESET);
        }
        System.out.println(Colors.ANSI_CYAN + "Inserisci il numero corrispondente all'opzione:" + ANSI_RESET);

        int clinput = Integer.parseInt(scanner.nextLine());
        if(clinput < 1 || clinput > argc) clinput = -1;

        return clinput;
    }

    int getInt(boolean limited, int min, int max, String title){
        boolean goback = true;
        int clinput = 0;
        while(goback){
            System.out.println(Colors.ANSI_CYAN + INSERT + title + " (numero)" + ANSI_RESET);

            try{
                clinput = Integer.parseInt(scanner.nextLine());
            }catch(Exception e){
                System.out.println(ANSI_YELLOW + "Devi inserire un numero!" + ANSI_RESET);
                continue;
            }

            if(limited){
                if(clinput < min || clinput > max){
                    System.out.println("Valore fuori dai limiti imposti! (min " + min + ", max: " + max + ")");
                } else {goback = false;}
            } else {goback = false;}
        }
        return clinput;
    }

    String getString(boolean limited, int minChars, int maxChars, String title){
        boolean goback = true;
        String clinput = "";
        while(goback){
            System.out.println(Colors.ANSI_CYAN + INSERT + title + ANSI_RESET);
            clinput = scanner.nextLine();
            if(limited){
                if(clinput.length() < minChars || clinput.length() > maxChars){
                    System.out.println("Valore fuori dai limiti imposti! (min " + minChars + " caratteri, max " + maxChars + " caratteri)");
                } else {goback = false;}
            } else {goback = false;}
        }
        return clinput;
    }

    int[] getDate(String title){
        System.out.println(Colors.ANSI_CYAN + INSERT + title + ANSI_RESET);
        int[] clinput = new int[3];
            clinput[0] = getInt(false, 0, 0, "Anno");
            clinput[1] = getInt(true, 1, 12, "Mese");
            clinput[2] = getInt(true, 1, LocalDate.of(clinput[0], clinput[1], 1).lengthOfMonth(), "Giorno");
        return clinput;
    }

}
