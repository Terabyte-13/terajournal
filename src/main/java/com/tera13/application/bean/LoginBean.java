package com.tera13.application.bean;

import com.tera13.application.exception.UserLoginException;

public class LoginBean {

    private String username;
    private String password;
    private String diaryListPath;

    public void setUsername(String s) throws UserLoginException {
        if(s == null || s.equals("")) {
            throw new UserLoginException("username invalido!");
        }else {
            username = s;
        }
    }

    public String getUsername() {
        return username;
    }

    // ---

    public void setPassword(String s) throws UserLoginException {
        if(s == null || s.equals("")) {
            throw new UserLoginException("password invalida!");
        }else {
            password = s;
        }
    }

    public String getPassword() {
        return password;
    }

    // ---

    public void setDiaryListPath(String s) throws UserLoginException {
        if(s == null) {
            throw new UserLoginException("diaryListPath invalido!");
        }else {
            diaryListPath = s;
        }
    }

    public String getDiaryListPath() {
        return diaryListPath;
    }



}
