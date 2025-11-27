package com.smjestaj.service;

import org.springframework.stereotype.Component;

import com.smjestaj.exception.RegisterException;
import com.smjestaj.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateEmail(String email) {
        if(userRepository.existsByEmail(email)) {
            throw new RegisterException("Email already exists!");
        }
        if(!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RegisterException("Invalid email format!");
        }
    }

    public void validatePassword(String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) {
            throw new RegisterException("Passwords do not match!");
        }
    }

    public void validateUsername(String username) {
        if(userRepository.existsByUsername(username)) {
            throw new RegisterException("Username already exists!");
        }
    }
}
