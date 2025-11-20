package com.smjestaj.dto;

import com.smjestaj.enums.UserRole;
import lombok.*;

@Getter
@Setter
public class RegisterData {
    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private UserRole role;
    //private String phoneNumber;
}
