package com.smjestaj.enums;

import lombok.*;

@RequiredArgsConstructor
@Getter
public enum ReservationStatus {
    PENDING("pending"),
    FIRST_ACTIVE("first active"),
    ACTIVE("active"),
    CANCELLED("cancelled");

    private final String displayName;
}
