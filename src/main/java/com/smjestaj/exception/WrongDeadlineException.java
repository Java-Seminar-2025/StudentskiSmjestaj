package com.smjestaj.exception;

public class WrongDeadlineException extends RuntimeException {
    public WrongDeadlineException(String message) {
        super(message);
    }
}
