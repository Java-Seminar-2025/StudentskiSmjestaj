package com.example.smjestaj.enums;

import lombok.*;

@AllArgsConstructor
@Getter
public enum UserRole {
    STUDENT("student"),
    LANDLORD("landlord");

    private final String displayName;
}
