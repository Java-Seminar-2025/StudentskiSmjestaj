package com.smjestaj.service;

import org.springframework.stereotype.Component;

import com.smjestaj.exception.RegisterException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private static final String REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    public void validateEmailFormat(String email) {
        if(!email.matches(REGEX)) {
            throw new RegisterException("Invalid email format!");
        }
    }

    public void validatePassword(String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) {
            throw new RegisterException("Passwords do not match!");
        }
    }
}
