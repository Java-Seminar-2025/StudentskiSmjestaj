package com.smjestaj.dto;

import com.smjestaj.enums.ReservationStatus;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record ReservationData (
    Long roomId,
    Long studentId,
    ReservationStatus status,
    LocalDateTime createdAt
) {}
