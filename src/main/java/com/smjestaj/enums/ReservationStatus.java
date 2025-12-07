package com.smjestaj.enums;

import lombok.*;

@RequiredArgsConstructor
@Getter
public enum ReservationStatus {
    PENDING("pending confirmation"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    COMPLETED("completed");

    private final String displayName;
}
