package com.smjestaj.enums;

import lombok.*;

@AllArgsConstructor
@Getter
public enum UserGender {
    MALE("male"),
    FEMALE("female");

    private final String displayName;
}
