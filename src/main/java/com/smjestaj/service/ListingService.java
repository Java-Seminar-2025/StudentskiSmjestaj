package com.smjestaj.service;

import com.smjestaj.dto.ListingData;
import com.smjestaj.dto.ListingFilters;
import com.smjestaj.dto.PageDto;
import com.smjestaj.enums.ListingStatus;
import com.smjestaj.exception.ListingNotFoundException;
import com.smjestaj.exception.WrongDeadlineException;
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
    private final ReservationService reservationService;
    private final StudentDetailsService studentDetailsService;

    public Page<ListingData> filterListings(ListingFilters listingFilters, PageDto pageDto) {
        var pageable = PageRequest.of(pageDto.page() - 1, pageDto.size(), Sort.by("price").ascending());

        var studentGender = studentDetailsService.getStudentData(homeService.getUsernameOfLoggedInUser()).gender();

        var listingsPage = listingRepository.findAll(
                ListingSpecification.withFilters(listingFilters, studentGender),
                pageable);

        return listingsPage.map(listingMapper::listingEntityToDto);
    }

    public Page<ListingData> findMyListings(PageDto pageDto) {
        var pageable = PageRequest.of(pageDto.page() - 1, pageDto.size(), Sort.by("id").ascending());
        var landlord = userRepository.findByUsername(homeService.getUsernameOfLoggedInUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        var myListingsPage = listingRepository.findAllByLandlordAndDeleted(landlord, false, pageable);
        return myListingsPage.map(listingMapper::listingEntityToDto);
    }

    public ListingData getListingById(Long listingId) {
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));
        return listingMapper.listingEntityToDto(listing);
    }

    public ListingData prepareNewListing() {
        var listingData = ListingData.builder().build();
        listingData = listingData.toBuilder().landlordUsername(homeService.getUsernameOfLoggedInUser()).build();
        return listingData;
    }

    public String createListing(ListingData listingData) {
        var landlord = userRepository.findByUsername(listingData.landlordUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        var listing = listingMapper.listingDtoToEntity(listingData);
        listing.setLandlord(landlord);
        listing.setStatus(ListingStatus.AVAILABLE);
        listing.setDeleted(false);

        if(listingData.daysToCancel() < 1) {
            throw new WrongDeadlineException("Days to cancel reservation cannot be smaller than 1.");
        }
        listing.setDaysToCancel(listingData.daysToCancel());

        listingRepository.save(listing);
        return "redirect:/listingRooms/create?listingId=" + listing.getId();
    }

    public void editListing(ListingData listingData) {
        var listing = listingRepository.findById(listingData.listingId())
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        listingMapper.updateEntityFromDto(listingData, listing);
        listing.updateDaysToCancel(listingData.daysToCancel());

        listingRepository.save(listing);
    }

    public void deleteListing(Long listingId) {
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));
        listing.setDeleted(true);
        listingRepository.save(listing);
    }

    public List<ListingData> getMostRecentListings() {
        var top3Listings = listingRepository.findTop3ByDeletedOrderByIdDesc(false);
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

    public boolean isFullListingReservable(Long listingId) {
        var hasPendingReservation = !reservationService.getPendingReservations(listingId).isEmpty();
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        return (!hasPendingReservation) && (listing.getStatus().equals(ListingStatus.AVAILABLE));
    }
}
