package com.smjestaj.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.ui.Model;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(RegisterException.class)
    public String handleRegisterException(RegisterException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "register";
    }

    @ExceptionHandler(LoginFailedException.class)
    public String handleLoginException(LoginFailedException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "login";
    }
}

