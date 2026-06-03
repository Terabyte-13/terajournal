package com.tera13.application.exception;

public class CreateDiaryException extends Exception {

    public CreateDiaryException(String message) {
        super(message);
    }

    public CreateDiaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
