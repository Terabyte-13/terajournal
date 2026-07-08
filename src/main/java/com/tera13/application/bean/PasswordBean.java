package com.tera13.application.bean;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PasswordBean {

    private String password;
    private String key; //la key generata utilizzando la password

    Logger logger = Logger.getLogger("passwordBean");

    public void setPassword(String p){
        if(p == null || p.equals("")){
            logger.log(Level.SEVERE, "qualcosa è andato storto! Non dovrebbero passare password NULL o \"\" per PasswordBean!");
            throw new IllegalArgumentException("qualcosa è andato storto! Non dovrebbero passare password NULL o \"\" per PasswordBean!");
        }
        password = p;
    }

    public String getPassword(){
        return password;
    }

    public void setKey(String k){
        if(k == null) throw new IllegalArgumentException("la key di un PasswordBean è stata impostata a NULL!");
        key = k;
    }

    public String getKey(){
        return key;
    }

}
