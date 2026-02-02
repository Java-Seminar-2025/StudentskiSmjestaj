package com.smjestaj.service;

import com.smjestaj.dto.*;
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

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

    public List<Long> getReservationsOfStudent(Long listingId, ReservationStatus status, ReservationType type) {
        var specifiers = ReservationSpecifiers.builder()
                .listingId(listingId)
                .studentUsername(homeService.getUsernameOfLoggedInUser())
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

    public List<ReservationData> getReservationsOfStudentForListing(Long listingId) {
        var specifiers = ReservationSpecifiers.builder()
                .listingId(listingId)
                .studentUsername(homeService.getUsernameOfLoggedInUser())
                .build();

        return reservationRepository.findAll(ReservationSpecification.withFilters(specifiers)).stream()
                .map(reservationMapper::reservationEntityToDto)
                .toList();
    }

    public List<ReservationData> getReservationsForRoom(Long roomId, Collection<ReservationStatus> statusList) {
        var specifiers = ReservationSpecifiers.builder()
                .roomId(roomId)
                .statusList(statusList)
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

        return reservationRepository.findAll(ReservationSpecification.withFilters(specifiers)).stream()
                .map(reservationMapper::reservationEntityToDto)
                .toList();
    }

    public void cancelOtherPendingReservations(ReservationEntity acceptedReservation) {
        var specifiers = ReservationSpecifiers.builder()
                .studentUsername(acceptedReservation.getStudent().getUsername())
                .status(ReservationStatus.PENDING)
                .build();

        var otherReservationsOfStudent = reservationRepository.findAll(ReservationSpecification.withFilters(specifiers));

        otherReservationsOfStudent.forEach(otherReservation -> {
            if (!otherReservation.getId().equals(acceptedReservation.getId())) {
                otherReservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(otherReservation);
            }
        });
    }

    public void acceptReservation(Long reservationId) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found!"));

        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.getListing().setStatus(reservation.getType().getCorrectListingStatus());

        reservationRepository.save(reservation);
        cancelOtherPendingReservations(reservation);
    }

    public List<MyReservationData> getListingsWithReservations() {
        var listingIds = reservationRepository.findListingIdsWithReservationsForStudent(homeService.getUsernameOfLoggedInUser());
        var listings = listingRepository.findAllByIdIn(listingIds);
        List<MyReservationData> myReservationDataList = new ArrayList<>();

        listings.forEach(listing -> {
            var reservations = getReservationsOfStudentForListing(listing.getId());
            var myReservationData = MyReservationData.builder()
                    .title(listing.getTitle())
                    .address(listing.getAddress())
                    .city(listing.getCity())
                    .landlordUsername(listing.getLandlord().getUsername())
                    .numberOfRooms(listing.getNumberOfRooms())
                    .status(getCorrectStatus(reservations))
                    .type(reservations.get(0).type())
                    .numberOfBookedRooms(getNumberOfBookedRooms(listing.getNumberOfRooms(), reservations))
                    .cancellationDeadline(listing.getCancellationDeadline())
                    .build();
            myReservationDataList.add(myReservationData);
        });

        return myReservationDataList;
    }

    public ReservationStatus getCorrectStatus(List<ReservationData> reservations) {
        boolean hasActive = reservations.stream()
                .anyMatch(reservation -> reservation.status() == ReservationStatus.ACTIVE);
        if (hasActive) {
            return ReservationStatus.ACTIVE;
        }

        boolean hasFirstActive = reservations.stream()
                .anyMatch(reservation -> reservation.status() == ReservationStatus.FIRST_ACTIVE);
        if (hasFirstActive) {
            return ReservationStatus.FIRST_ACTIVE;
        }

        return ReservationStatus.PENDING;
    }

    public Integer getNumberOfBookedRooms(Integer numberOfListingRooms, List<ReservationData> reservations) {
        if (reservations.get(0).type().equals(ReservationType.ROOM)) {
            return reservations.size();
        }
        return numberOfListingRooms;
    }
}