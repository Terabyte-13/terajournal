package com.tera13.application.bean;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DateBean {

    private int year;
    private int month;
    private int day;

    Logger logger = Logger.getLogger("DateBean");

    public void setYear(int y){
        year = y;
    }

    public int getYear(){
        return year;
    }

    //------

    public void setMonth(int m){
        if(m > 0 && m < 13){
            month = m;
        } else {
            logger.log(Level.SEVERE, "DateBean ha ricevuto un mese invalido! ({0})", m);
            throw new IllegalArgumentException("Mese invalido!");
        }
    }

    public int getMonth(){
        return month;
    }

    //------

    public void setDay(int d){
        int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
        if(d > 0 && d <= daysInMonth){
            day = d;
        } else {
            logger.log(Level.SEVERE, "DateBean ha ricevuto un giorno invalido! {0} nel mese {1}. \nRicorda di impostare prima l anno e il mese!",new Object[]{d, month});
            throw new IllegalArgumentException("Giorno invalido!");
        }
    }

    public int getDay(){
        return day;
    }

}
