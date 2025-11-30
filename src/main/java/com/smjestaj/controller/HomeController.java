package com.smjestaj.controller;

import com.smjestaj.dto.SafeUserData;
import com.smjestaj.entity.UserEntity;
import com.smjestaj.mapper.UserMapper;
import com.smjestaj.repository.UserRepository;

import org.springframework.security.core.Authentication;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        SafeUserData userData = userMapper.userEntityToSafeUserData(user);

        model.addAttribute("userData", userData);
        return "home";
    }
}

