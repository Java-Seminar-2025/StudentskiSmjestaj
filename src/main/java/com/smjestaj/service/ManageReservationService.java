package com.smjestaj.service;

import com.smjestaj.entity.ReservationEntity;
import com.smjestaj.exception.ListingNotFoundException;
import com.smjestaj.exception.ReservationNotFoundException;
import com.smjestaj.exception.RoomNotFoundException;
import com.smjestaj.repository.ListingRepository;
import com.smjestaj.repository.ReservationRepository;
import com.smjestaj.repository.RoomRepository;
import com.smjestaj.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ManageReservationService {
    private final FilterReservationsService filterReservationsService;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final ListingRepository listingRepository;
    private final HomeService homeService;

    public void addNewRoomReservation(Long roomId) {
        var student = userRepository.findByUsername(homeService.getUsernameOfLoggedInUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found!"));

        var reservation = ReservationEntity.createRoomReservation(student, room);
        reservationRepository.save(reservation);
    }

    public void addNewFullReservation(Long listingId) {
        var student = userRepository.findByUsername(homeService.getUsernameOfLoggedInUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        var reservation = ReservationEntity.createFullListingReservation(student, listing);
        reservationRepository.save(reservation);
    }

    public void updateCancellationDeadline(Long listingId) {
        filterReservationsService.getActiveReservationsForListing(listingId)
                .forEach(reservationData -> {
                    var reservationId = filterReservationsService.getReservationIdByListingId(listingId);
                    var reservation = reservationRepository.findById(reservationId)
                            .orElseThrow(() -> new ReservationNotFoundException("Reservation not found!"));
                    reservation.setCancellationDeadline(LocalDateTime.now().plusDays(reservation.getListing().getDaysToCancel()));
                    reservationRepository.save(reservation);
                });
    }
}
