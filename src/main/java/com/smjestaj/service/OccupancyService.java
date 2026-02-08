package com.smjestaj.service;

import com.smjestaj.dto.ReservationData;
import com.smjestaj.enums.ListingStatus;
import com.smjestaj.exception.ListingNotFoundException;
import com.smjestaj.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OccupancyService {
    private final ListingRepository listingRepository;

    public void updateListingStatus(Long listingId, List<ReservationData> activeReservations) {
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found!"));

        if(activeReservations.size() == listing.getNumberOfStudents()) {
            listing.setStatus(ListingStatus.OCCUPIED);
            listingRepository.save(listing);
            return;
        }

        if(activeReservations.isEmpty()) {
            listing.setStatus(ListingStatus.AVAILABLE);
            listingRepository.save(listing);
            return;
        }

        listing.setStatus(ListingStatus.PARTIALLY_OCCUPIED);
        listingRepository.save(listing);
    }
}
