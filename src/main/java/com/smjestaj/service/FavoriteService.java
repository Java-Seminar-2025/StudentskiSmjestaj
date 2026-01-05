package com.smjestaj.service;

import com.smjestaj.entity.ListingEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smjestaj.entity.FavoriteEntity;
import com.smjestaj.repository.ListingRepository;
import com.smjestaj.repository.FavoriteRepository;
import com.smjestaj.repository.UserRepository;
import com.smjestaj.exception.ListingNotFoundException;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final HomeService homeService;

    public void addFavorite(Long listingId) {
        var username = homeService.getLoggedInUser();
        var student = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        var existingFavorites = favoriteRepository.findAllByStudentAndListing(student, listing);

        if(!existingFavorites.isEmpty()) {
            existingFavorites.forEach(favoriteEntity -> {
                favoriteEntity.setSaved(true);
                favoriteRepository.save(favoriteEntity);
            });
            return;
        }

        var favorite = new FavoriteEntity();

        favorite.setListing(listing);
        favorite.setStudent(student);
        favorite.setSaved(true);

        favoriteRepository.save(favorite);
    }

    public void removeFavorite(Long listingId) {
        var username = homeService.getLoggedInUser();
        var student = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        favoriteRepository.findAllByStudentAndListing(student, listing)
                .forEach(favoriteEntity -> {
                    favoriteEntity.setSaved(false);
                    favoriteRepository.save(favoriteEntity);
                });
    }

    public List<Long> findAllFavoritesOfUser() {
        var username = homeService.getLoggedInUser();
        var student = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        return favoriteRepository.findAllBySavedAndStudent(true, student).stream()
                .map(FavoriteEntity::getListing)
                .map(ListingEntity::getId)
                .toList();
    }
}
