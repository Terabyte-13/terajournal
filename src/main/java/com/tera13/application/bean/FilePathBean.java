package com.tera13.application.bean;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FilePathBean {
    private String path;
    Logger logger = Logger.getLogger("FilePathBean");

    public void setPath(String p){
        if(p == null){
            logger.log(Level.WARNING, "Un FilePathBean è stato impostato a NULL!");
        }
        path = p;
    }

    public String getPath(){
        return path;
    }
}
