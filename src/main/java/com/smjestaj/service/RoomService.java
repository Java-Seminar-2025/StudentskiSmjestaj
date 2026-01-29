package com.smjestaj.service;

import com.smjestaj.dto.AllRoomsData;
import com.smjestaj.dto.RoomData;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import com.smjestaj.exception.ListingNotFoundException;
import com.smjestaj.exception.RoomNotFoundException;
import com.smjestaj.mapper.RoomMapper;
import com.smjestaj.repository.ListingRepository;
import com.smjestaj.repository.RoomRepository;

import com.smjestaj.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final ListingRepository listingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomMapper roomMapper;
    private final ReservationService reservationService;
    private final HomeService homeService;

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

    public AllRoomsData getAllRoomsDataOfListing(Long listingId, ReservationStatus status) {
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        var user = userRepository.findByUsername(homeService.getLoggedInUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        var listingRooms = roomRepository.findAllByListing(listing).stream()
                .map(roomMapper::roomEntityToDto)
                .map(roomData -> {
                    roomData.setReservations(reservationService.getReservationsForRoom(roomData.getRoomId(), status));
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
                    var isOccupied = roomData.getCapacity() == roomData.getReservations().size();
                    var isAlreadyBookedByStudent = roomReservationsOfStudent.contains(roomData.getRoomId());

                    var isReservable = (!hasActiveReservationsForListing) && (!hasFullReservationForListing) &&
                                        (!isAlreadyBookedByStudent) && (!isOccupied);
                    roomData.setIsReservable(isReservable);
                });
    }
}
