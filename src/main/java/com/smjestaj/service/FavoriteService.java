package com.smjestaj.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smjestaj.entity.FavoriteEntity;
import com.smjestaj.repository.ListingRepository;
import com.smjestaj.repository.FavoriteRepository;
import com.smjestaj.repository.UserRepository;
import com.smjestaj.exception.ListingNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final HomeService homeService;

    public void addFavorite(Long listingId) {
        var username = homeService.getLoggedInUser();
        var favorite = new FavoriteEntity();

        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));
        var student = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        favorite.setListing(listing);
        favorite.setStudent(student);

        favoriteRepository.save(favorite);
    }
}
