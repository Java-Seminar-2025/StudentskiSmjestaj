package com.example.smjestaj.enums;

public enum ListingStatus {
    available("available"),
    partially_occupied("partially occupied"),
    occupied("occupied");

    private final String displayName;

    ListingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
