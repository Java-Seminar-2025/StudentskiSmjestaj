package com.smjestaj.enums;

import lombok.*;

@RequiredArgsConstructor
@Getter
public enum UserRole {
    STUDENT("student"),
    LANDLORD("landlord"),
    ADMIN("admin");

    private final String displayName;
}
