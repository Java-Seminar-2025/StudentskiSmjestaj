package com.smjestaj.service;

import com.smjestaj.dto.ListingData;
import com.smjestaj.dto.OptionsData;
import com.smjestaj.dto.PageDto;
import com.smjestaj.entity.*;
import com.smjestaj.mapper.ListingMapper;
import com.smjestaj.repository.*;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingService {
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final ListingMapper listingMapper;
    private final HomeService homeService;

    public Page<ListingData> filterListings(OptionsData optionsData, PageDto pageDto) {
        var pageable = PageRequest.of(pageDto.page() - 1, pageDto.size(), Sort.by("price").ascending());

        var listingsPage = listingRepository.findAll(
                ListingSpecification.withFilters(optionsData),
                pageable);

        return listingsPage.map(listingMapper::listingEntityToDto);
    }

    public Page<ListingData> findMyListings(PageDto pageDto) {
        var pageable = PageRequest.of(pageDto.page() - 1, pageDto.size(), Sort.by("id").ascending());
        var landlord = userRepository.findByUsername(homeService.getLoggedInUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        var myListingsPage = listingRepository.findAllByLandlord(landlord, pageable);
        return myListingsPage.map(listingMapper::listingEntityToDto);
    }

    public ListingData prepareNewListing() {
        var listingData = ListingData.builder().build();
        listingData = listingData.toBuilder().landlordUsername(homeService.getLoggedInUser()).build();
        return listingData;
    }

    public String createListing(ListingData listingData) {
        var landlord = userRepository.findByUsername(listingData.landlordUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        var listing = listingMapper.listingDtoToEntity(listingData);
        listing.setLandlord(landlord);

        listingRepository.save(listing);
        return "redirect:/listingRooms/create?listingId=" + listing.getId();
    }

    public List<ListingData> getMostRecentListings() {
        var top3Listings = listingRepository.findTop3ByOrderByIdDesc();
        return top3Listings.stream()
                .map(listingMapper::listingEntityToDto)
                .toList();
    }

    public PageDto changePage(PageDto pageDto, String action) {
        return switch (action) {
            case "first" -> (pageDto.page() == 1)
                    ? pageDto
                    : pageDto.toBuilder().page(1).build();
            case "prev" -> (pageDto.page() > 1)
                    ? pageDto.toBuilder().page(pageDto.page() - 1).build()
                    : pageDto;
            case "next" -> (pageDto.page() < pageDto.totalPages())
                    ? pageDto.toBuilder().page(pageDto.page() + 1).build()
                    : pageDto;
            case "last" -> (pageDto.page().equals(pageDto.totalPages()))
                    ? pageDto
                    : pageDto.toBuilder().page(pageDto.totalPages()).build();
            default -> pageDto;
        };
    }
}
