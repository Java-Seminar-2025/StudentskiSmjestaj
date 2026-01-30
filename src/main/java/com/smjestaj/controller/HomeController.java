package com.smjestaj.controller;

import com.smjestaj.service.HomeService;
import com.smjestaj.service.ListingService;

import com.smjestaj.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;
    private final UserService userService;
    private final ListingService listingService;

    @GetMapping("/home")
    public String showHomePage(Model model) {
        var username = homeService.getUsernameOfLoggedInUser();
        model.addAttribute("userData", userService.getUserData(username));
        model.addAttribute("top3Listings", listingService.getMostRecentListings());
        return "home";
    }
}

