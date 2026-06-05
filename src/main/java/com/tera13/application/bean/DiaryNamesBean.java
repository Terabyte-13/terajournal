package com.tera13.application.bean;

import java.util.List;

public class DiaryNamesBean {

    private List<String> names;

    public void setNames(List<String> n){
        if(n == null){
            throw new IllegalArgumentException("DiaryNamesBean è stato impostato a null!");
            //non la gestisco perchè se qua arriva NULL, metadataParser avrà già tirato una MetadataParserException
        }
        names = n;
    }

    public List<String> getNames(){
        return names;
    }

}
