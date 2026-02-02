package com.smjestaj.dto;

import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MyReservationData (
    String title,
    String address,
    String city,
    String landlordUsername,
    Integer numberOfRooms,
    ReservationStatus status,
    ReservationType type,
    Integer numberOfBookedRooms,
    LocalDateTime cancellationDeadline
) {}
