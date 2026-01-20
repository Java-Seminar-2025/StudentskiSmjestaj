package com.smjestaj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.smjestaj.service.FavoriteService;
import com.smjestaj.service.ListingService;
import com.smjestaj.dto.OptionsData;
import com.smjestaj.dto.PageDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final ListingService listingService;

    @PostMapping("/add")
    public String addFavorite(@ModelAttribute PageDto pageDto,
                              @ModelAttribute OptionsData optionsData,
                              @RequestParam Long listingId,
                              Model model) {

        favoriteService.addFavorite(listingId);

        var favorites = favoriteService.findAllFavoritesOfStudent();
        var listings = listingService.filterListings(optionsData, pageDto);

        model.addAttribute("listings", listings);
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("optionsData", optionsData);
        model.addAttribute("favorites", favorites);
        return "listings";
    }

    @PostMapping("/remove")
    public String removeFavorite(@ModelAttribute PageDto pageDto,
                                 @ModelAttribute OptionsData optionsData,
                                 @RequestParam Long listingId,
                                 Model model) {

        favoriteService.removeFavorite(listingId);

        var favorites = favoriteService.findAllFavoritesOfStudent();
        var listings = listingService.filterListings(optionsData, pageDto);

        model.addAttribute("listings", listings);
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("optionsData", optionsData);
        model.addAttribute("favorites", favorites);
        return "listings";
    }
}
