package com.smjestaj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.smjestaj.service.FavoriteService;
import com.smjestaj.service.ListingService;
import com.smjestaj.dto.ListingFilters;
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
                              @ModelAttribute ListingFilters listingFilters,
                              @RequestParam Long listingId,
                              Model model) {

        favoriteService.addFavorite(listingId);

        var favorites = favoriteService.findAllFavoritesOfStudent();
        var listings = listingService.filterListings(listingFilters, pageDto);

        model.addAttribute("listings", listings);
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("listingFilters", listingFilters);
        model.addAttribute("favorites", favorites);
        return "listings";
    }

    @PostMapping("/remove")
    public String removeFavorite(@ModelAttribute PageDto pageDto,
                                 @ModelAttribute ListingFilters listingFilters,
                                 @RequestParam Long listingId,
                                 Model model) {

        favoriteService.removeFavorite(listingId);

        var favorites = favoriteService.findAllFavoritesOfStudent();
        var listings = listingService.filterListings(listingFilters, pageDto);

        model.addAttribute("listings", listings);
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("listingFilters", listingFilters);
        model.addAttribute("favorites", favorites);
        return "listings";
    }

    @GetMapping("/show")
    public String showMyFavoritesPage(Model model) {
        var pageDto = PageDto.builder().build();
        pageDto = pageDto.toBuilder().page(1).size(10).build();

        var favoritesList = favoriteService.getFavoritesPage(pageDto);
        pageDto = pageDto.toBuilder().totalPages(favoritesList.getTotalPages()).build();

        model.addAttribute("pageDto", pageDto);
        model.addAttribute("favoritesList", favoritesList);
        return "myFavorites";
    }

    @PostMapping("/changePage")
    public String changeMyFavoritesPage(@ModelAttribute PageDto pageDto,
                                        @RequestParam String action,
                                        Model model) {

        pageDto = listingService.changePage(pageDto, action);
        var favoritesList = favoriteService.getFavoritesPage(pageDto);

        model.addAttribute("pageDto", pageDto);
        model.addAttribute("favoritesList", favoritesList);

        return "myFavorites";
    }
}
