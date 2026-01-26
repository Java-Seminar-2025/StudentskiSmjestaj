package com.smjestaj.enums;

public enum ReservationType {
    ROOM {
        @Override
        public ListingStatus getCorrectListingStatus() {
            return ListingStatus.PARTIALLY_OCCUPIED;
        }
    },
    FULL_LISTING {
        @Override
        public ListingStatus getCorrectListingStatus() {
            return ListingStatus.OCCUPIED;
        }
    };

    public abstract ListingStatus getCorrectListingStatus();
}
