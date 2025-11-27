package com.smjestaj.controller;

import com.smjestaj.dto.*;
import com.smjestaj.service.UserService;
import com.smjestaj.exception.RegisterException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerData", new RegisterData());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterData input) {
        userService.register(input);
        return "redirect:/user/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginData", new LoginData());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginData input, Model model) {
        SafeUserData userDto = userService.login(input);
        model.addAttribute("userData", userDto);
        return "login_success";
    }
}
