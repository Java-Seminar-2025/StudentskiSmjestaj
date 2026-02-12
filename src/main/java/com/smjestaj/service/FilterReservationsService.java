package com.smjestaj.service;

import com.smjestaj.dto.ReservationData;
import com.smjestaj.dto.ReservationSpecifiers;
import com.smjestaj.entity.ListingEntity;
import com.smjestaj.entity.ListingRoomEntity;
import com.smjestaj.entity.ReservationEntity;
import com.smjestaj.enums.ListingStatus;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import com.smjestaj.exception.ListingNotFoundException;
import com.smjestaj.mapper.ReservationMapper;
import com.smjestaj.repository.ListingRepository;
import com.smjestaj.repository.ReservationRepository;
import com.smjestaj.repository.ReservationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilterReservationsService {
    private final HomeService homeService;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final ListingRepository listingRepository;

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

    public List<Long> getPendingReservationsOfStudent(Long listingId) {
        return getReservationsOfStudent(listingId, EnumSet.of(ReservationStatus.PENDING), null);
    }

    public List<Long> getPendingRoomReservationsOfStudent(Long listingId) {
        return getReservationsOfStudent(listingId, EnumSet.of(ReservationStatus.PENDING), ReservationType.ROOM);
    }

    public List<Long> getPendingFullReservationsOfStudent(Long listingId) {
        return getReservationsOfStudent(listingId, EnumSet.of(ReservationStatus.PENDING), ReservationType.FULL_LISTING);
    }

    public List<Long> getActiveReservationsOfStudent(Long listingId) {
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

    public List<ReservationData> filterReservations(Long listingId,
                                                    Long roomId,
                                                    Collection<ReservationStatus> statusList,
                                                    ReservationType type) {
        var specifiers = ReservationSpecifiers.builder()
                .listingId(listingId)
                .roomId(roomId)
                .statusList(statusList)
                .type(type)
                .excludeCancelled(true)
                .build();

        return reservationRepository.findAll(ReservationSpecification.withFilters(specifiers)).stream()
                .map(reservationMapper::reservationEntityToDto)
                .toList();
    }

    public List<ReservationData> getReservationsForListing(Long listingId) {
        return filterReservations(listingId, null, null, null);
    }

    public List<ReservationData> getActiveReservationsForListing(Long listingId) {
        var statusList = EnumSet.of(ReservationStatus.ACTIVE, ReservationStatus.FIRST_ACTIVE);
        return filterReservations(listingId, null, statusList, null);
    }

    public List<ReservationData> getReservationsForRoom(Long roomId, Collection<ReservationStatus> statusList) {
        return filterReservations(null, roomId, statusList, null);
    }

    public List<ReservationData> getFullListingReservations(Long listingId, Collection<ReservationStatus> statusList) {
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        return filterReservations(listingId, null, statusList, ReservationType.FULL_LISTING).stream()
                .map(reservation -> reservation.toBuilder()
                        .isAcceptable(listing.getStatus().equals(ListingStatus.AVAILABLE))
                        .build())
                .toList();
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
}
