package com.tera13.application.bean;

import com.tera13.application.exception.CreateDiaryException;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class CreateDiaryBean {

    private String name;
    private String path; //path della cartella
    private String password;
    private String confirmPassword;

    public void setName(String s) throws CreateDiaryException {
        if(s == null || s.equals("")) {
            throw new CreateDiaryException("Il nome del CreateDiaryBean è invalido!");
        }else {
            name = s;
        }
    }

    public String getName() {
        return name;
    }

    //------

    public void setPath(String p) throws CreateDiaryException {
        if(p == null || p.equals("")) {
            throw new CreateDiaryException("Il percorso del CreateDiaryBean è invalido!");
        }else {
            try{ //controllo se il path è invalido per qualche altro motivo
                Paths.get(p);
            }catch(InvalidPathException e){
                throw new CreateDiaryException("Il percorso del CreateDiaryBean è invalido!");
            }
            path = p;
        }
    }

    public String getPath() {
        return path;
    }

    //------

    public void setPassword(String p) throws CreateDiaryException {
        if(p == null) { //la password puo essere vuota, ma se qualcosa la imposta a null c'è un problema
            throw new CreateDiaryException("La password del CreateDiaryBean è invalido!");
        }else {
            password = p;
        }
    }

    public String getPassword() {
        return password;
    }

    //------

    public void setConfirmPassword(String p) throws CreateDiaryException {
        if(!p.equals(password)) {
            throw new CreateDiaryException("ConfirmPassword non coincide con Password nel CreateDiaryBean!");
        }else {
            confirmPassword = p;
        }
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

}
