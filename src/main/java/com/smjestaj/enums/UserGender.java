package com.smjestaj.enums;

import lombok.*;

@RequiredArgsConstructor
@Getter
public enum UserGender {
    MALE("male"),
    FEMALE("female");

    private final String displayName;
}
