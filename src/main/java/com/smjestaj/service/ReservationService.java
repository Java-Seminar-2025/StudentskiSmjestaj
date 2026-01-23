package com.smjestaj.service;

import com.smjestaj.dto.AllRoomsData;
import com.smjestaj.entity.ReservationEntity;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.exception.ReservationNotFoundException;
import com.smjestaj.exception.RoomNotFoundException;
import com.smjestaj.mapper.ReservationMapper;
import com.smjestaj.repository.ReservationRepository;
import com.smjestaj.repository.RoomRepository;
import com.smjestaj.repository.UserRepository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final HomeService homeService;

    public void addNewReservation(Long roomId) {
        var student = userRepository.findByUsername(homeService.getLoggedInUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found!"));

        var reservation = ReservationEntity.createPendingReservation(student, room);
        reservationRepository.save(reservation);
    }

    public void getReservationsForEachRoom(AllRoomsData listingRooms) {
        listingRooms.getRooms()
                .forEach(roomData -> {
                    var roomEntity = roomRepository.findById(roomData.getRoomId())
                            .orElseThrow(() -> new RoomNotFoundException("Room not found!"));

                    var reservations = reservationRepository.findAllByRoomAndStatus(roomEntity, ReservationStatus.PENDING);

                    roomData.setReservations(reservations.stream()
                            .map(reservationMapper::reservationEntityToDto)
                            .toList());
                });
    }

    public void acceptReservation(Long reservationId) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found!"));
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservationRepository.save(reservation);
    }
}
