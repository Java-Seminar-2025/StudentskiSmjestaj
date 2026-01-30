package com.smjestaj.dto;

import lombok.Builder;

@Builder
public record ListingFilters (
    Integer lowerPrice,
    Integer upperPrice,
    String city,
    Integer numberOfStudents,
    Integer numberOfRooms
) {}
