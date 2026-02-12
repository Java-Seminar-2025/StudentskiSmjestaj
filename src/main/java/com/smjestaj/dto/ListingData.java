package com.smjestaj.dto;

import com.smjestaj.enums.*;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder(toBuilder = true)
public record ListingData (
    Long listingId,
    String landlordUsername,
    String title,
    String description,
    String address,
    String city,
    Double price,
    Integer numberOfStudents,
    Integer numberOfRooms,
    UserGender preferredGender,
    ListingStatus status,

    @Min(value = 1, message = "Days to cancel cannot be smaller than 1.")
    Integer daysToCancel,

    Boolean isReservable,
    Boolean deleted
) {}