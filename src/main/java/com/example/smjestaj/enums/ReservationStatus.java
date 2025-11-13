package com.example.smjestaj.enums;

import lombok.*;

@AllArgsConstructor
@Getter
public enum ReservationStatus {
    PENDING("pending confirmation"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    COMPLETED("completed");

    private final String displayName;
}
