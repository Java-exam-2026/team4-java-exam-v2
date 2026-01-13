package com.javaexam.exception;

public class AlreadySubmittedException extends RuntimeException {
    public AlreadySubmittedException(String message) {
        super(message);
    }
}
