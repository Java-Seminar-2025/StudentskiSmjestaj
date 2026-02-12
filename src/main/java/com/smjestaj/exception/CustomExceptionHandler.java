package com.smjestaj.exception;

import com.smjestaj.dto.RegisterData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.ui.Model;

@ControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler {
    @ExceptionHandler(RegisterException.class)
    public String handleRegisterException(RegisterException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("registerData", RegisterData.builder().build());
        return "register";
    }

    @ExceptionHandler({
            ListingNotFoundException.class,
            RoomNotFoundException.class,
            ReservationNotFoundException.class,
            StudentDetailsNotFoundException.class
    })
    public String handleNotFound(RuntimeException ex, Model model) {

        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("statusCode", 404);

        return "globalErrorPage";
    }
}

