package com.smjestaj.dto;

import com.smjestaj.enums.UserRole;
import lombok.*;

@Getter
@Setter
public class ChangeRoleDto {
    private String username;
    private UserRole role;
}
