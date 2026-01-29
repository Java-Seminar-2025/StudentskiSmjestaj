package com.smjestaj.service;

import com.smjestaj.dto.ListingData;
import com.smjestaj.dto.PageDto;
import com.smjestaj.dto.ReservationData;
import com.smjestaj.dto.ReservationSpecifiers;
import com.smjestaj.entity.ListingEntity;
import com.smjestaj.entity.ListingRoomEntity;
import com.smjestaj.entity.ReservationEntity;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import com.smjestaj.exception.ListingNotFoundException;
import com.smjestaj.exception.ReservationNotFoundException;
import com.smjestaj.exception.RoomNotFoundException;
import com.smjestaj.mapper.ListingMapper;
import com.smjestaj.mapper.ReservationMapper;
import com.smjestaj.repository.*;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final ListingMapper listingMapper;
    private final HomeService homeService;

    public void addNewRoomReservation(Long roomId) {
        var student = userRepository.findByUsername(homeService.getLoggedInUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found!"));

        var reservation = ReservationEntity.createRoomReservation(student, room);
        reservationRepository.save(reservation);
    }

    public void addNewFullReservation(Long listingId) {
        var student = userRepository.findByUsername(homeService.getLoggedInUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        var reservation = ReservationEntity.createFullListingReservation(student, listing);
        reservationRepository.save(reservation);
    }

    public List<Long> getReservationsOfStudent(Long listingId, ReservationStatus status, ReservationType type) {
        var specifiers = ReservationSpecifiers.builder()
                .listingId(listingId)
                .studentUsername(homeService.getLoggedInUser())
                .status(status)
                .type(type)
                .build();

        var reservationsOfStudent = reservationRepository.findAll(ReservationSpecification.withFilters(specifiers));

        if (specifiers.type() == ReservationType.ROOM) {
            return reservationsOfStudent.stream()
                    .map(ReservationEntity::getRoom)
                    .map(ListingRoomEntity::getId)
                    .toList();
        }

        return reservationsOfStudent.stream()
                .map(ReservationEntity::getListing)
                .map(ListingEntity::getId)
                .toList();
    }

    public List<Long> getPendingReservations(Long listingId) {
        return getReservationsOfStudent(listingId, ReservationStatus.PENDING, null);
    }

    public List<Long> getPendingRoomReservations(Long listingId) {
        return getReservationsOfStudent(listingId, ReservationStatus.PENDING, ReservationType.ROOM);
    }

    public List<Long> getPendingFullReservations(Long listingId) {
        return getReservationsOfStudent(listingId, ReservationStatus.PENDING, ReservationType.FULL_LISTING);
    }

    public List<Long> getActiveReservations(Long listingId) {
        return getReservationsOfStudent(listingId, ReservationStatus.ACTIVE, null);
    }

    public List<ReservationData> getReservationsForRoom(Long roomId, ReservationStatus status) {
        var specifiers = ReservationSpecifiers.builder()
                .roomId(roomId)
                .status(status)
                .build();

        return reservationRepository.findAll(ReservationSpecification.withFilters(specifiers)).stream()
                .map(reservationMapper::reservationEntityToDto)
                .toList();
    }

    public List<ReservationData> getFullListingReservations(Long listingId) {
        var specifiers = ReservationSpecifiers.builder()
                .listingId(listingId)
                .status(ReservationStatus.PENDING)
                .type(ReservationType.FULL_LISTING)
                .build();

        var reservations = reservationRepository.findAll(ReservationSpecification.withFilters(specifiers));

        return reservations.stream()
                .map(reservationMapper::reservationEntityToDto)
                .toList();
    }

    public void acceptReservation(Long reservationId) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found!"));

        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.getListing().setStatus(reservation.getType().getCorrectListingStatus());

        reservationRepository.save(reservation);

        var student = reservation.getStudent();
        var otherReservationsOfStudent = reservationRepository.findAllByStudent(student);

        otherReservationsOfStudent.forEach(otherReservation -> {
            if(otherReservation.getId() != reservationId) {
                otherReservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(otherReservation);
            }
        });
    }

    public Page<ListingData> getMyReservationsPage(PageDto pageDto) {
        var username = homeService.getLoggedInUser();
        var student = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        Pageable pageable = PageRequest.of(pageDto.page() - 1, pageDto.size(), Sort.by("id").ascending());

        var reservationPage = reservationRepository.findAllByStudent(student, pageable);

        return reservationPage.map(reservation ->
                listingMapper.listingEntityToDto(reservation.getListing())
        );
    }
}
