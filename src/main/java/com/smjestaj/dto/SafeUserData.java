package com.smjestaj.dto;

import com.smjestaj.enums.UserRole;
import lombok.*;

@AllArgsConstructor
public class SafeUserData {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String username;
    private UserRole role;
    private String phoneNumber;
}
