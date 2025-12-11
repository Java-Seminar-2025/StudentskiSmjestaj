package com.smjestaj.dto;

import lombok.Builder;

@Builder
public record OptionsData (
    Integer lowerPrice,
    Integer upperPrice,
    String city,
    Integer numberOfStudents,
    Integer numberOfRooms
) {}
