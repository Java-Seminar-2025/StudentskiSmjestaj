package com.smjestaj.dto;

import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record ReservationData (
    Long reservationId,
    Long roomId,
    Long listingId,
    String studentUsername,
    ReservationStatus status,
    LocalDateTime createdAt,
    ReservationType type,
    LocalDateTime cancellationDeadline,
    Boolean isAcceptable
) {}
