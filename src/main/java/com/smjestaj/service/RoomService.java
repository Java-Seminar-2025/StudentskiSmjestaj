package com.smjestaj.service;

import com.smjestaj.dto.AllRoomsData;
import com.smjestaj.dto.ReservationData;
import com.smjestaj.dto.RoomData;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.exception.ListingNotFoundException;
import com.smjestaj.exception.RoomNotFoundException;
import com.smjestaj.mapper.RoomMapper;
import com.smjestaj.repository.ListingRepository;
import com.smjestaj.repository.RoomRepository;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final ListingRepository listingRepository;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final ReservationService reservationService;

    public AllRoomsData prepareRoomForms(Long listingId) {
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        List<RoomData> rooms = new ArrayList<>();

        for (int i = 0; i < listing.getNumberOfRooms(); i++) {
            rooms.add(new RoomData());
        }

        return new AllRoomsData(listingId, rooms);
    }

    public void addListingRooms(AllRoomsData allRoomsData) {
        var listing = listingRepository.findById(allRoomsData.getListingId())
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        allRoomsData.getRooms().stream()
                .map(roomMapper::roomDtoToEntity)
                .forEach(room -> {
                    room.setListing(listing);
                    roomRepository.save(room);
                });
    }

    public AllRoomsData getAllRoomsDataOfListing(Long listingId, Collection<ReservationStatus> statusList) {
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        var listingRooms = roomRepository.findAllByListing(listing).stream()
                .map(roomMapper::roomEntityToDto)
                .map(roomData -> {
                    var reservations = reservationService.getReservationsForRoom(roomData.getRoomId(), statusList).stream()
                            .map(reservationData -> reservationService.setAcceptableForReservation(reservationData, roomData))
                            .toList();

                    roomData.setReservations(reservations);
                    return roomData;
                })
                .toList();

        return new AllRoomsData(listingId, listingRooms);
    }

    public Long getListingIdByRoomId(Long roomId) {
        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found!"));
        return room.getListing().getId();
    }

    public void setReservableForEachRoom(AllRoomsData allRoomsData) {
        var roomReservationsOfStudent = reservationService.getPendingRoomReservations(allRoomsData.getListingId());
        var hasFullReservationForListing = !reservationService.getPendingFullReservations(allRoomsData.getListingId()).isEmpty();
        var hasActiveReservationsForListing = !reservationService.getActiveReservations(allRoomsData.getListingId()).isEmpty();

        allRoomsData.getRooms()
                .forEach(roomData -> {
                    var isOccupied = roomData.getCapacity().equals(roomData.getReservations().size());
                    var isAlreadyBookedByStudent = roomReservationsOfStudent.contains(roomData.getRoomId());

                    var isReservable = (!hasActiveReservationsForListing) && (!hasFullReservationForListing) &&
                                       (!isAlreadyBookedByStudent) && (!isOccupied);
                    roomData.setIsReservable(isReservable);
                });
    }

    public AllRoomsData getBookedRoomsData(Long listingId, List<ReservationData> reservations) {
        List<RoomData> rooms = new ArrayList<>();
        reservations.forEach(reservation -> {
            var room = roomRepository.findById(reservation.roomId())
                    .orElseThrow(() -> new RoomNotFoundException("Room not found!"));
            rooms.add(roomMapper.roomEntityToDto(room));
        });
        return new AllRoomsData(listingId, rooms);
    }
}
