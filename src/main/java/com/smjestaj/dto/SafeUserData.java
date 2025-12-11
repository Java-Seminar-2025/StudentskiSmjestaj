package com.smjestaj.dto;

import com.smjestaj.enums.UserRole;
import lombok.Builder;

@Builder
public record SafeUserData (
    String name,
    String surname,
    String email,
    String username,
    UserRole role,
    String phoneNumber,
    Boolean blocked
) {}
