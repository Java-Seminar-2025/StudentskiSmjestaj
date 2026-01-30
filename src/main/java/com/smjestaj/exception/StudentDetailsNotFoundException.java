package com.smjestaj.exception;

public class StudentDetailsNotFoundException extends RuntimeException {
    public StudentDetailsNotFoundException(String message) {
        super(message);
    }
}
