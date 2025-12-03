package com.smjestaj.dto;

import com.smjestaj.enums.UserRole;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class SafeUserData {
    private String name;
    private String surname;
    private String email;
    private String username;
    private UserRole role;
    private String phoneNumber;
    private Boolean blocked;
}
