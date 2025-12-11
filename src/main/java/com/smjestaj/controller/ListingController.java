package com.smjestaj.controller;

import com.smjestaj.dto.ListingData;
import com.smjestaj.dto.OptionsData;
import com.smjestaj.service.ListingService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/listings")
@RequiredArgsConstructor
public class ListingController {
    private final ListingService listingService;

    @GetMapping("/options")
    public String showListingOptions(Model model) {
        model.addAttribute("optionsData", OptionsData.builder().build());
        return "listingOptions";
    }

    @PostMapping("/options")
    public String showFilteredListings(@ModelAttribute OptionsData optionsData, Model model) {
        var listings = listingService.filterListings(optionsData, 0, 10);
        model.addAttribute("listings", listings);
        return "listings";
    }

    @GetMapping("/create")
    public String showCreateListingPage(Model model) {
        var listingData = listingService.prepareNewListing();
        model.addAttribute("listingData", listingData);
        return "createListing";
    }

    @PostMapping("/create")
    public String createListing(@ModelAttribute ListingData listingData) {
        listingService.createListing(listingData);
        return "redirect:/home";
    }
}
