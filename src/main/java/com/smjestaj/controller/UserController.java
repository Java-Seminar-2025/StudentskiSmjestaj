package com.smjestaj.controller;

import com.smjestaj.dto.*;
import com.smjestaj.enums.UserRole;
import com.smjestaj.service.StudentDetailsService;
import com.smjestaj.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final StudentDetailsService studentDetailsService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerData", RegisterData.builder().build());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterData input) {
        userService.register(input);
        return userService.redirectToCorrectPage(input.username(), input.role());
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginData", LoginData.builder().build());
        return "login";
    }

    @GetMapping("/{username}")
    public String showUserInfoPage(@PathVariable String username, Model model) {
        var userData = userService.getUserData(username);
        model.addAttribute("userData", userData);
        var studentData = userData.role().equals(UserRole.STUDENT) ? studentDetailsService.getStudentData(username) : null;
        model.addAttribute("studentData", studentData);
        return "userInfo";
    }
}
