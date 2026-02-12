package com.smjestaj.service;

import com.smjestaj.dto.ReservationSpecifiers;
import com.smjestaj.entity.ReservationEntity;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.exception.ListingNotFoundException;
import com.smjestaj.exception.ReservationNotFoundException;
import com.smjestaj.repository.ListingRepository;
import com.smjestaj.repository.ReservationRepository;
import com.smjestaj.repository.ReservationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CancelReservationService {
    private final ReservationRepository reservationRepository;
    private final FilterReservationsService filterReservationsService;
    private final HomeService homeService;
    private final OccupancyService occupancyService;
    private final ListingRepository listingRepository;

    public void cancelReservation(ReservationEntity reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservationRepository.save(reservation);
    }

    public void cancelRoomReservation(Long roomId) {
        var reservation = reservationRepository.findById(filterReservationsService.getReservationIdByRoomId(roomId))
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found!"));
        cancelReservation(reservation);
    }

    public void cancelOtherPendingReservationsOfStudent(ReservationEntity acceptedReservation) {
        var specifiers = ReservationSpecifiers.builder()
                .studentUsername(acceptedReservation.getStudent().getUsername())
                .status(ReservationStatus.PENDING)
                .build();

        var otherReservationsOfStudent = reservationRepository.findAll(ReservationSpecification.withFilters(specifiers));

        otherReservationsOfStudent.forEach(otherReservation -> {
            if (!otherReservation.getId().equals(acceptedReservation.getId())) {
                cancelReservation(otherReservation);
            }
        });
    }

    public void cancelFirstActiveReservation(Long listingId) {
        var reservation = reservationRepository.findById(filterReservationsService.getReservationIdByListingId(listingId))
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found!"));

        cancelReservation(reservation);

        changeOtherActiveReservationsToPending(listingId);
        occupancyService.updateListingStatus(listingId, filterReservationsService.getActiveReservationsForListing(listingId));
    }

    public void cancelMyReservationsForListing(Long listingId) {
        var specifiers = ReservationSpecifiers.builder()
                .listingId(listingId)
                .studentUsername(homeService.getUsernameOfLoggedInUser())
                .build();
        var myReservationsForListing = reservationRepository.findAll(ReservationSpecification.withFilters(specifiers));

        myReservationsForListing.forEach(myReservation -> {
            if(myReservation.getStatus().equals(ReservationStatus.FIRST_ACTIVE)) {
                changeOtherActiveReservationsToPending(listingId);
            }
            cancelReservation(myReservation);
        });

        occupancyService.updateListingStatus(listingId, filterReservationsService.getActiveReservationsForListing(listingId));
    }

    public void changeOtherActiveReservationsToPending(Long listingId) {
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));
        var activeReservations = reservationRepository.findAllByListingAndStatus(listing, ReservationStatus.ACTIVE);

        activeReservations.forEach(activeReservation -> {
            activeReservation.setStatus(ReservationStatus.PENDING);
            activeReservation.setAcceptedAt(null);
            activeReservation.setCancellationDeadline(null);
            reservationRepository.save(activeReservation);
        });
    }

    public void cancelAllReservationsForDeletedListing(Long listingId) {
        var reservationsForListing = filterReservationsService.getReservationsForListing(listingId);

        reservationsForListing.forEach(reservationData -> {
            var reservation = reservationRepository.findById(reservationData.reservationId())
                    .orElseThrow(() -> new ReservationNotFoundException("Reservation not found!"));
            cancelReservation(reservation);
        });
    }
}
