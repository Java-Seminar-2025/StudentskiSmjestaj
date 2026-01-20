package com.smjestaj.service;

import com.smjestaj.dto.AllRoomsData;
import com.smjestaj.dto.RoomData;
import com.smjestaj.exception.ListingNotFoundException;
import com.smjestaj.mapper.RoomMapper;
import com.smjestaj.repository.ListingRepository;
import com.smjestaj.repository.RoomRepository;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final ListingRepository listingRepository;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public AllRoomsData prepareRoomForms(Long listingId) {
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        List<RoomData> rooms = new ArrayList<>();

        for (int i = 0; i < listing.getNumberOfRooms(); i++) {
            rooms.add(new RoomData());
        }

        return new AllRoomsData(listingId, listing.getNumberOfRooms(), rooms);
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

    public AllRoomsData showRoomsOfListing(Long listingId) {
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        var listingRooms = roomRepository.findAllByListing(listing).stream()
                .map(roomMapper::roomEntityToDto)
                .toList();

        return new AllRoomsData(listingId, listing.getNumberOfRooms(), listingRooms);
    }
}
