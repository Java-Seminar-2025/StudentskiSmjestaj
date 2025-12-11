package com.smjestaj.dto;

import com.smjestaj.enums.UserRole;
import lombok.Builder;

@Builder
public record RegisterData (
    String name,
    String surname,
    String username,
    String email,
    String password,
    String confirmPassword,
    UserRole role,
    String phoneNumber
) {}
