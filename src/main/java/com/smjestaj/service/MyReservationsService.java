package com.smjestaj.service;

import com.smjestaj.dto.MyReservationData;
import com.smjestaj.dto.ReservationData;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import com.smjestaj.mapper.ListingMapper;
import com.smjestaj.repository.ListingRepository;
import com.smjestaj.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyReservationsService {
    private final ReservationRepository reservationRepository;
    private final HomeService homeService;
    private final ListingRepository listingRepository;
    private final ListingMapper listingMapper;
    private final OccupancyService occupancyService;
    private final FilterReservationsService filterReservationsService;

    public List<MyReservationData> getListingsWithMyReservations() {
        var listingIds = reservationRepository.findListingIdsWithReservationsForStudent(homeService.getUsernameOfLoggedInUser());
        var listings = listingRepository.findAllByIdIn(listingIds);
        List<MyReservationData> myReservationDataList = new ArrayList<>();

        listings.forEach(listing -> {
            occupancyService.updateListingStatus(listing.getId(), filterReservationsService.getActiveReservationsForListing(listing.getId()));

            var listingReservations = filterReservationsService.getReservationsOfStudentForListing(listing.getId());
            var myReservationData = listingMapper.listingEntityToMyReservationData(listing);
            var cancellationDeadline = listingReservations.get(0).cancellationDeadline();

            myReservationData = myReservationData.toBuilder()
                    .status(listingReservations.get(0).status())
                    .type(listingReservations.get(0).type())
                    .cancellationDeadline(cancellationDeadline)
                    .numberOfBookedRooms(getNumberOfBookedRooms(listing.getNumberOfRooms(), listingReservations))
                    .isCancellable(cancellationDeadline == null || LocalDateTime.now().isBefore(cancellationDeadline))
                    .build();

            myReservationDataList.add(myReservationData);
        });

        return myReservationDataList;
    }

    public Integer getNumberOfBookedRooms(Integer numberOfListingRooms, List<ReservationData> reservations) {
        if (reservations.get(0).type().equals(ReservationType.ROOM)) {
            return reservations.size();
        }
        return numberOfListingRooms;
    }
}
