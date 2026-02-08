package com.smjestaj.controller;

import com.smjestaj.dto.ListingData;
import com.smjestaj.dto.ListingFilters;
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

    @GetMapping("/filters")
    public String showListingFilters(Model model) {
        model.addAttribute("listingFilters", ListingFilters.builder().build());
        return "listingFilters";
    }

    @PostMapping("/filters")
    public String showFilteredListings(@ModelAttribute ListingFilters listingFilters, Model model) {
        var pageDto = PageDto.builder().build();
        pageDto = pageDto.toBuilder().page(1).size(10).returnPage("listings").build();

        var listings = listingService.filterListings(listingFilters, pageDto);
        pageDto = pageDto.toBuilder().totalPages(listings.getTotalPages()).build();

        var favorites = favoriteService.findAllFavoritesOfStudent();

        model.addAttribute("listings", listings);
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("favorites", favorites);
        return "listings";
    }

    @PostMapping("/changePage")
    public String changeListingsPage(@ModelAttribute PageDto pageDto,
                                     @ModelAttribute ListingFilters listingFilters,
                                     @RequestParam String action,
                                     Model model) {

        pageDto = listingService.changePage(pageDto, action);
        var listings = listingService.filterListings(listingFilters, pageDto);
        var favorites = favoriteService.findAllFavoritesOfStudent();

        model.addAttribute("listings", listings);
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("listingFilters", listingFilters);
        model.addAttribute("favorites", favorites);

        return "listings";
    }

    @GetMapping("/myListings")
    public String showMyListings(Model model) {
        var pageDto = PageDto.builder().build();
        pageDto = pageDto.toBuilder().page(1).size(10).build();

        var myListings = listingService.findMyListings(pageDto);
        pageDto = pageDto.toBuilder().totalPages(myListings.getTotalPages()).build();

        model.addAttribute("myListings", myListings);
        model.addAttribute("pageDto", pageDto);

        return "myListings";
    }

    @PostMapping("/myListings")
    public String postMyListings() {
        return "redirect:/listings/myListings";
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

    @GetMapping("/edit")
    public String showEditListingPage(@RequestParam Long listingId, Model model) {
        model.addAttribute("listingData", listingService.getListingById(listingId));
        return "editListing";
    }

    @PostMapping("/edit")
    public String editListing(@ModelAttribute ListingData listingData, Model model) {
        listingService.editListing(listingData);
        return "redirect:/listings/myListings";
    }

    @PostMapping("/delete")
    public String deleteListing(@RequestParam Long listingId) {
        listingService.deleteListing(listingId);
        return "redirect:/listings/myListings";
    }

    @GetMapping("/showDetails")
    public String showListingDetails(@RequestParam Long listingId, Model model) {
        model.addAttribute("listing", listingService.getListingById(listingId));
        return "listingDetails";
    }
}
