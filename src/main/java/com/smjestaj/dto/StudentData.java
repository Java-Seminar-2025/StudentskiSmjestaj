package com.smjestaj.dto;

import com.smjestaj.enums.UserGender;
import lombok.Builder;

@Builder(toBuilder = true)
public record StudentData (
    Long studentId,
    String facultyName,
    String facultyAddress,
    String facultyCity,
    Integer yearOfStudy,
    UserGender gender
) {}
