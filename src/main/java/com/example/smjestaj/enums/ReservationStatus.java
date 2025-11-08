package com.example.smjestaj.enums;

public enum ReservationStatus {
    pending("pending confirmation"),
    active("active"),
    cancelled("cancelled"),
    completed("completed");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
