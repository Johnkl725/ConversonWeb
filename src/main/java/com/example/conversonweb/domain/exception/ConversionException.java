package com.example.conversonweb.domain.exception;

/**
 * Custom exception for conversion-related errors
 */
public class ConversionException extends Exception {
    private final String fileName;

    public ConversionException(String message) {
        super(message);
        this.fileName = null;
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
        this.fileName = null;
    }

    public ConversionException(String fileName, String message) {
        super(message);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
