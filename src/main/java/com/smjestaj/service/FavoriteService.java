package com.smjestaj.service;

import com.smjestaj.dto.ListingData;
import com.smjestaj.dto.PageDto;
import com.smjestaj.entity.ListingEntity;
import com.smjestaj.mapper.ListingMapper;

import org.springframework.data.domain.*;
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
    private final ListingMapper listingMapper;
    private final UserRepository userRepository;
    private final HomeService homeService;

    public void addFavorite(Long listingId) {
        var username = homeService.getUsernameOfLoggedInUser();
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
        var username = homeService.getUsernameOfLoggedInUser();
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

    public List<Long> findAllFavoriteIdsOfStudent() {
        var username = homeService.getUsernameOfLoggedInUser();
        var student = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        return favoriteRepository.findAllBySavedAndStudent(true, student).stream()
                .map(FavoriteEntity::getListing)
                .map(ListingEntity::getId)
                .toList();
    }

    public Page<ListingData> getFavoritesPage(PageDto pageDto) {
        var username = homeService.getUsernameOfLoggedInUser();
        var student = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        Pageable pageable = PageRequest.of(pageDto.page() - 1, pageDto.size(), Sort.by("id").ascending());

        var favoritesPage = favoriteRepository.findAllBySavedAndStudent(true, student, pageable);

        return favoritesPage.map(favorite ->
                listingMapper.listingEntityToDto(favorite.getListing())
        );
    }
}
