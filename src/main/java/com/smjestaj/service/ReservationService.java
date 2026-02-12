package com.smjestaj.service;

import com.smjestaj.dto.*;
import com.smjestaj.entity.ListingEntity;
import com.smjestaj.entity.ListingRoomEntity;
import com.smjestaj.entity.ReservationEntity;
import com.smjestaj.enums.ListingStatus;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import com.smjestaj.enums.UserRole;
import com.smjestaj.exception.ListingNotFoundException;
import com.smjestaj.exception.ReservationNotFoundException;
import com.smjestaj.exception.RoomNotFoundException;
import com.smjestaj.mapper.ListingMapper;
import com.smjestaj.mapper.ReservationMapper;
import com.smjestaj.repository.*;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
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
    private final UserService userService;
    private final OccupancyService occupancyService;

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

    public List<Long> getReservationsOfStudent(Long listingId, Collection<ReservationStatus> statusList, ReservationType type) {
        var specifiers = ReservationSpecifiers.builder()
                .listingId(listingId)
                .studentUsername(homeService.getUsernameOfLoggedInUser())
                .statusList(statusList)
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
        return getReservationsOfStudent(listingId, EnumSet.of(ReservationStatus.PENDING), null);
    }

    public List<Long> getPendingRoomReservations(Long listingId) {
        return getReservationsOfStudent(listingId, EnumSet.of(ReservationStatus.PENDING), ReservationType.ROOM);
    }

    public List<Long> getPendingFullReservations(Long listingId) {
        return getReservationsOfStudent(listingId, EnumSet.of(ReservationStatus.PENDING), ReservationType.FULL_LISTING);
    }

    public List<Long> getActiveReservations(Long listingId) {
        return getReservationsOfStudent(listingId, EnumSet.of(ReservationStatus.ACTIVE, ReservationStatus.FIRST_ACTIVE), null);
    }

    public List<ReservationData> getReservationsOfStudentForListing(Long listingId) {
        var specifiers = ReservationSpecifiers.builder()
                .listingId(listingId)
                .studentUsername(homeService.getUsernameOfLoggedInUser())
                .excludeCancelled(true)
                .build();

        return reservationRepository.findAll(ReservationSpecification.withFilters(specifiers)).stream()
                .map(reservationMapper::reservationEntityToDto)
                .toList();
    }

    public List<ReservationData> getReservationsForListing(Long listingId, Collection<ReservationStatus> statusList) {
        var specifiers = ReservationSpecifiers.builder()
                .listingId(listingId)
                .statusList(statusList)
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

    public List<ReservationData> getFullListingReservations(Long listingId, Collection<ReservationStatus> statusList) {
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        var specifiers = ReservationSpecifiers.builder()
                .listingId(listingId)
                .statusList(statusList)
                .type(ReservationType.FULL_LISTING)
                .build();

        return reservationRepository.findAll(ReservationSpecification.withFilters(specifiers)).stream()
                .map(reservationMapper::reservationEntityToDto)
                .map(reservation -> reservation.toBuilder()
                        .isAcceptable(listing.getStatus().equals(ListingStatus.AVAILABLE))
                        .build())
                .toList();
    }

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
        cancelOtherPendingReservationsOfStudent(reservation);
    }

    public void cancelOtherPendingReservationsOfStudent(ReservationEntity acceptedReservation) {
        var specifiers = ReservationSpecifiers.builder()
                .studentUsername(acceptedReservation.getStudent().getUsername())
                .status(ReservationStatus.PENDING)
                .build();

        var otherReservationsOfStudent = reservationRepository.findAll(ReservationSpecification.withFilters(specifiers));

        otherReservationsOfStudent.forEach(otherReservation -> {
            if (!otherReservation.getId().equals(acceptedReservation.getId())) {
                otherReservation.setStatus(ReservationStatus.CANCELLED);
                otherReservation.setCancelledAt(LocalDateTime.now());
                reservationRepository.save(otherReservation);
            }
        });
    }

    public ReservationData setAcceptableForRoomReservation(ReservationData reservationData, RoomData roomData) {
        var statusList = EnumSet.of(ReservationStatus.ACTIVE, ReservationStatus.FIRST_ACTIVE);
        var activeReservationsForRoom = getReservationsForRoom(roomData.getRoomId(), statusList);

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

    public List<MyReservationData> getListingsWithMyReservations() {
        var listingIds = reservationRepository.findListingIdsWithReservationsForStudent(homeService.getUsernameOfLoggedInUser());
        var listings = listingRepository.findAllByIdIn(listingIds);
        var statusList = EnumSet.of(ReservationStatus.ACTIVE, ReservationStatus.FIRST_ACTIVE);
        List<MyReservationData> myReservationDataList = new ArrayList<>();

        listings.forEach(listing -> {
            occupancyService.updateListingStatus(listing.getId(), getReservationsForListing(listing.getId(), statusList));

            var listingReservations = getReservationsOfStudentForListing(listing.getId());
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

    public Long getReservationIdByRoomId(Long roomId) {
        var specifiers = ReservationSpecifiers.builder()
                .studentUsername(homeService.getUsernameOfLoggedInUser())
                .roomId(roomId)
                .excludeCancelled(true)
                .build();

        return reservationRepository.findAll(ReservationSpecification.withFilters(specifiers)).get(0).getId();
    }

    public Long getReservationIdByListingId(Long listingId) {
        var specifiers = ReservationSpecifiers.builder()
                .listingId(listingId)
                .status(ReservationStatus.FIRST_ACTIVE)
                .build();

        return reservationRepository.findAll(ReservationSpecification.withFilters(specifiers)).get(0).getId();
    }

    public void cancelReservation(ReservationEntity reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservationRepository.save(reservation);
    }

    public void cancelRoomReservation(Long roomId) {
        var reservation = reservationRepository.findById(getReservationIdByRoomId(roomId))
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found!"));
        cancelReservation(reservation);
    }

    public void cancelFirstActiveReservation(Long listingId) {
        var reservation = reservationRepository.findById(getReservationIdByListingId(listingId))
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found!"));
        var statusList = EnumSet.of(ReservationStatus.ACTIVE, ReservationStatus.FIRST_ACTIVE);

        cancelReservation(reservation);

        changeOtherActiveReservationsToPending(listingId);
        occupancyService.updateListingStatus(listingId, getReservationsForListing(listingId, statusList));
    }

    public void cancelMyReservationsForListing(Long listingId) {
        var specifiers = ReservationSpecifiers.builder()
                .listingId(listingId)
                .studentUsername(homeService.getUsernameOfLoggedInUser())
                .build();
        var statusList = EnumSet.of(ReservationStatus.ACTIVE, ReservationStatus.FIRST_ACTIVE);
        var myReservationsForListing = reservationRepository.findAll(ReservationSpecification.withFilters(specifiers));

        myReservationsForListing.forEach(myReservation -> {
                if(myReservation.getStatus().equals(ReservationStatus.FIRST_ACTIVE)) {
                    changeOtherActiveReservationsToPending(listingId);
                }
                cancelReservation(myReservation);
            });

        occupancyService.updateListingStatus(listingId, getReservationsForListing(listingId, statusList));
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
        var statusList = EnumSet.of(ReservationStatus.PENDING, ReservationStatus.ACTIVE, ReservationStatus.FIRST_ACTIVE);
        var reservationsForListing = getReservationsForListing(listingId, statusList);

        reservationsForListing.forEach(reservationData -> {
            var reservation = reservationRepository.findById(reservationData.reservationId())
                    .orElseThrow(() -> new ReservationNotFoundException("Reservation not found!"));
            cancelReservation(reservation);
        });
    }
}