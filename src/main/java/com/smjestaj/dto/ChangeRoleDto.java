package com.smjestaj.dto;

import com.smjestaj.enums.UserRole;
import lombok.*;

@Builder
public record ChangeRoleDto (
    String username,
    UserRole role
) {}
