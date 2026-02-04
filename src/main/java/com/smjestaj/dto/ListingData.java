package com.smjestaj.dto;

import com.smjestaj.enums.*;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

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
    LocalDateTime cancellationDeadline,
    ListingStatus status,
    Boolean isReservable,
    Boolean deleted
) {}