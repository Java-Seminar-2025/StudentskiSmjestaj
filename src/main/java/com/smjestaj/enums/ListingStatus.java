package com.smjestaj.enums;

import lombok.*;

@RequiredArgsConstructor
@Getter
public enum ListingStatus {
    AVAILABLE("available"),
    PARTIALLY_OCCUPIED("partially occupied"),
    OCCUPIED("occupied");

    private final String displayName;
}
