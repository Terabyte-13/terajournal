package com.tera13.application;

import com.tera13.application.frontend.JavafxStarter;

/*RICORDA: se il programma non parte perchè javafx non trova la variabile d'ambiente DISPLAY
esegui "xhost +si:localuser:$(whoami)" nel terminale*/

public class Main {
	public static void main(String[] args) {
        JavafxStarter j = new JavafxStarter();
		j.l(args);
	}
}
