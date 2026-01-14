package com.smjestaj.controller;

import com.smjestaj.dto.ListingData;
import com.smjestaj.dto.OptionsData;
import com.smjestaj.dto.PageDto;
import com.smjestaj.service.FavoriteService;
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
    private final FavoriteService favoriteService;

    @GetMapping("/options")
    public String showListingOptions(Model model) {
        model.addAttribute("optionsData", OptionsData.builder().build());
        return "listingOptions";
    }

    @PostMapping("/options")
    public String showFilteredListings(@ModelAttribute OptionsData optionsData, Model model) {
        var pageDto = PageDto.builder().build();
        pageDto = pageDto.toBuilder().page(1).size(10).build();

        var listings = listingService.filterListings(optionsData, pageDto);
        pageDto = pageDto.toBuilder().totalPages(listings.getTotalPages()).build();

        var favorites = favoriteService.findAllFavoritesOfUser();

        model.addAttribute("listings", listings);
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("favorites", favorites);
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
        return listingService.createListing(listingData);
    }

    @PostMapping("/changePage")
    public String changeListingsPage(@ModelAttribute PageDto pageDto,
                                     @ModelAttribute OptionsData optionsData,
                                     @RequestParam String action,
                                     Model model) {

        pageDto = listingService.changePage(pageDto, action);
        var listings = listingService.filterListings(optionsData, pageDto);
        var favorites = favoriteService.findAllFavoritesOfUser();

        model.addAttribute("listings", listings);
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("optionsData", optionsData);
        model.addAttribute("favorites", favorites);

        return "listings";
    }

    @PostMapping("/myListings")
    public String showMyListingsPage(Model model) {
        var pageDto = PageDto.builder().build();
        pageDto = pageDto.toBuilder().page(1).size(10).build();

        var myListings = listingService.findMyListings(pageDto);
        pageDto = pageDto.toBuilder().totalPages(myListings.getTotalPages()).build();

        model.addAttribute("myListings", myListings);
        model.addAttribute("pageDto", pageDto);

        return "myListings";
    }

    @PostMapping("/changeMyListingsPage")
    public String changeMyListingsPage(@ModelAttribute PageDto pageDto,
                                       @RequestParam String action,
                                       Model model) {

        pageDto = listingService.changePage(pageDto, action);
        var myListings = listingService.findMyListings(pageDto);

        model.addAttribute("myListings", myListings);
        model.addAttribute("pageDto", pageDto);

        return "myListings";
    }
}
