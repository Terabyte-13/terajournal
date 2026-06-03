package com.tera13.application.exception;

public class MetadataParserException extends Exception{

    public MetadataParserException(String message) {
        super(message);
    }

    public MetadataParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
