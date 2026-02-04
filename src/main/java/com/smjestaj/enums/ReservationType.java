package com.smjestaj.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReservationType {
    ROOM("room") {
        @Override
        public ListingStatus getCorrectListingStatus() {
            return ListingStatus.PARTIALLY_OCCUPIED;
        }
    },
    FULL_LISTING("full listing") {
        @Override
        public ListingStatus getCorrectListingStatus() {
            return ListingStatus.OCCUPIED;
        }
    };

    private final String displayName;
    public abstract ListingStatus getCorrectListingStatus();
}
