package com.smjestaj.controller;

import com.smjestaj.dto.ListingData;
import com.smjestaj.dto.OptionsData;
import com.smjestaj.dto.SafeUserData;
import com.smjestaj.entity.ListingEntity;
import com.smjestaj.service.ListingService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Controller
@RequestMapping("/listings")
@RequiredArgsConstructor
public class ListingController {
    private final ListingService listingService;

    @GetMapping("/options")
    public String showListingOptions(Model model) {
        model.addAttribute(new OptionsData());
        return "listingOptions";
    }

    @PostMapping("/options")
    public String showFilteredListings(@ModelAttribute OptionsData optionsData, Model model) {
        List<ListingData> listings = listingService.filterListings(optionsData);
        model.addAttribute("listings", listings);
        return "listings";
    }

    @GetMapping("/create")
    public String showCreateListingPage(Model model) {
        ListingData listingData = listingService.prepareNewListing();
        model.addAttribute("listingData", listingData);
        return "createListing";
    }


    @PostMapping("/create")
    public String createListing(@ModelAttribute ListingData listingData) {
        listingService.createListing(listingData);
        return "redirect:/home";
    }
}
