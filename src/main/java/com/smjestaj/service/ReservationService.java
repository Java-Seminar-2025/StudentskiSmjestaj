package com.smjestaj.service;

import com.smjestaj.entity.ReservationEntity;
import com.smjestaj.exception.RoomNotFoundException;
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
}
