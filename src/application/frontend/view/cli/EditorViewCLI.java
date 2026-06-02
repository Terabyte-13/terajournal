package application.frontend.view.cli;

import static application.frontend.view.cli.Colors.*;
import static application.frontend.view.cli.Colors.ANSI_CYAN;
import static application.frontend.view.cli.Colors.ANSI_RESET;

public class EditorViewCLI extends ViewCLI {

    int y;
    int m;
    int d;
    Selector s = new Selector();
    Boolean stay = true;

    public EditorViewCLI(int year, int month, int day){
        y = year;
        m = month;
        d = day;
    }

    public void show(){
        System.out.println(ANSI_CYAN_BG + ANSI_BLACK + "   t e r a j o u r n a l   " + ANSI_RESET);
        System.out.println(ANSI_CYAN + "Benvenuto/a! Scegli un'opzione:" + ANSI_RESET);
        while(Boolean.TRUE.equals(stay)) edit();
    }

    void edit(){
        String data;
        try{
            data = sm.loadPage(y, m, d);
        }catch(Exception e){
            data = ""; //TODO vedi se conviene metterci l'html
        }
        System.out.println(ANSI_CYAN_BG + ANSI_BLACK + "Pagina: " + y + "/" + m + "/" + d + ANSI_RESET);
        System.out.println(data);

        String clinput = s.getString(false, 0, 0, "una riga da aggiungere alla pagina");
        System.out.println(ANSI_CYAN + "(Inserisci una riga vuota per uscire)" + ANSI_RESET);
        if(clinput.equals("")) stay = false; //se si inserisce una riga vuota, torna alla schermata iniziale
        sm.savePage(data + clinput + "<br>" + System.lineSeparator(), y, m, d);
        //br per l'html. lineseparator per rendere il file piu' ordinato
    }
}
