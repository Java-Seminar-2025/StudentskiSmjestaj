package com.smjestaj.service;

import com.smjestaj.dto.ListingData;
import com.smjestaj.dto.ListingFilters;
import com.smjestaj.dto.PageDto;
import com.smjestaj.enums.ListingStatus;
import com.smjestaj.enums.UserRole;
import com.smjestaj.exception.ListingNotFoundException;
import com.smjestaj.exception.ReservationNotFoundException;
import com.smjestaj.mapper.ListingMapper;
import com.smjestaj.repository.*;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ListingService {
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final ListingMapper listingMapper;
    private final HomeService homeService;
    private final FilterReservationsService filterReservationsService;
    private final ManageReservationService manageReservationService;
    private final CancelReservationService cancelReservationService;
    private final UserService userService;
    private final StudentDetailsService studentDetailsService;
    private final FavoriteService favoriteService;

    public Page<ListingData> filterListings(ListingFilters listingFilters, PageDto pageDto) {
        var pageable = PageRequest.of(pageDto.page() - 1, pageDto.size(), Sort.by("price").ascending());

        var username = homeService.getUsernameOfLoggedInUser();
        var userRole = userService.getUserData(username).role();

        var studentGender = (userRole.equals(UserRole.STUDENT))
                ? studentDetailsService.getStudentData(username).gender()
                : null;

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

        listingRepository.save(listing);
        return "redirect:/listingRooms/create?listingId=" + listing.getId();
    }

    public Optional<String> editListing(ListingData listingData) {
        var listing = listingRepository.findById(listingData.listingId())
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        listingMapper.updateEntityFromDto(listingData, listing);

        Optional<String> errorMessage = listing.updateDaysToCancel(listingData.daysToCancel());
        if (errorMessage.isEmpty()) {
            listingRepository.save(listing);
            manageReservationService.updateCancellationDeadline(listingData.listingId());
        }

        return errorMessage;
    }

    public void deleteListing(Long listingId) {
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));
        listing.setDeleted(true);

        listingRepository.save(listing);

        cancelReservationService.cancelAllReservationsForDeletedListing(listingId);
        favoriteService.unfavoriteDeletedListing(listing.getId());
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
        var hasPendingReservation = !filterReservationsService.getPendingReservationsOfStudent(listingId).isEmpty();
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        return (!hasPendingReservation) && (listing.getStatus().equals(ListingStatus.AVAILABLE));
    }
}
