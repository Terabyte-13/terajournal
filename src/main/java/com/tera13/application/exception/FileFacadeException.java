package com.tera13.application.exception;

public class FileFacadeException extends Exception{

    public FileFacadeException(String message) {
        super(message);
    }

    public FileFacadeException(String message, Throwable cause) {
        super(message, cause);
    }
}
