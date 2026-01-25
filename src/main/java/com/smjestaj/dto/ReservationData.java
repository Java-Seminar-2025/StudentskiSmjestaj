package com.smjestaj.dto;

import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record ReservationData (
    Long reservationId,
    Long roomId,
    String studentUsername,
    ReservationStatus status,
    LocalDateTime createdAt,
    ReservationType type
) {}
