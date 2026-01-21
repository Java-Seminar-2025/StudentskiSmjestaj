package com.smjestaj.exception;

import com.smjestaj.dto.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.ui.Model;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(RegisterException.class)
    public String handleRegisterException(RegisterException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("registerData", RegisterData.builder().build());
        return "register";
    }

    @ExceptionHandler(ListingNotFoundException.class)
    public String handleListingNotFoundException(ListingNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("listingData", ListingData.builder().build());
        return "createListing";
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public String handleRoomNotFoundException(RoomNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("roomData", new RoomData());
        return "createListing";
    }
}

