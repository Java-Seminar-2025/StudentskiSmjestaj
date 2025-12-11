package com.smjestaj.controller;

import com.smjestaj.mapper.UserMapper;
import com.smjestaj.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/home")
    public String showHomePage(Model model) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var username = auth.getName();

        var user = userRepository.findByUsername(username).orElseThrow();
        var userData = userMapper.userEntityToSafeUserData(user);

        model.addAttribute("userData", userData);
        return "home";
    }
}

