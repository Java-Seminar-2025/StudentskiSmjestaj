package com.smjestaj.service;

import com.smjestaj.dto.ListingData;
import com.smjestaj.dto.OptionsData;
import com.smjestaj.entity.*;
import com.smjestaj.mapper.ListingMapper;
import com.smjestaj.repository.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingService {
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final ListingMapper listingMapper;

    public List<ListingData> filterListings(OptionsData optionsData) {
        return listingRepository.findAll(ListingSpecification.withFilters(optionsData)).stream()
                .map(listingMapper::listingEntityToDto)
                .collect(Collectors.toList());
    }

    public ListingData prepareNewListing() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        ListingData listingData = new ListingData();
        listingData.setLandlordUsername(username);
        return listingData;
    }

    public void createListing(ListingData listingData) {
        var listing = listingMapper.listingDtoToEntity(listingData);
        listingRepository.save(listing);
    }
}
