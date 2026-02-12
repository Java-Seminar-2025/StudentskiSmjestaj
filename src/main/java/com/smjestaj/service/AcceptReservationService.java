package com.smjestaj.service;

import com.smjestaj.dto.ReservationData;
import com.smjestaj.dto.RoomData;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.UserRole;
import com.smjestaj.exception.ReservationNotFoundException;
import com.smjestaj.repository.ReservationRepository;
import com.smjestaj.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class AcceptReservationService {
    private final ReservationRepository reservationRepository;
    private final FilterReservationsService filterReservationsService;
    private final CancelReservationService cancelReservationService;
    private final UserRepository userRepository;
    private final HomeService homeService;
    private final UserService userService;

    public void acceptReservation(Long reservationId) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found!"));
        var loggedInUser = userRepository.findByUsername(homeService.getUsernameOfLoggedInUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        reservation.setStatus(loggedInUser.getRole().getCorrectReservationStatus());
        if(loggedInUser.getRole().equals(UserRole.LANDLORD)) {
            reservation.getListing().setStatus(reservation.getType().getCorrectListingStatus());
        }
        reservation.setAcceptedAt(LocalDateTime.now());
        reservation.setCancellationDeadline(LocalDateTime.now().plusDays(reservation.getListing().getDaysToCancel()));

        reservationRepository.save(reservation);
        cancelReservationService.cancelOtherPendingReservationsOfStudent(reservation);
    }

    public ReservationData setAcceptableForRoomReservation(ReservationData reservationData, RoomData roomData) {
        var statusList = EnumSet.of(ReservationStatus.ACTIVE, ReservationStatus.FIRST_ACTIVE);
        var activeReservationsForRoom = filterReservationsService.getReservationsForRoom(roomData.getRoomId(), statusList);

        return reservationData.toBuilder()
                .isAcceptable(reservationData.status().equals(ReservationStatus.PENDING) &&
                        (activeReservationsForRoom.size() < roomData.getCapacity()))
                .build();
    }

    public String redirectToCorrectPage(Long reservationId) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found!"));
        var userData = userService.getUserData(homeService.getUsernameOfLoggedInUser());

        return userData.role().equals(UserRole.STUDENT)
                ? "redirect:/reservations/manage?listingId=" + reservation.getListing().getId()
                : "redirect:/listings/myListings";
    }
}
