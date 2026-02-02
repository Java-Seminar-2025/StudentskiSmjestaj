package com.smjestaj.dto;

import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import lombok.Builder;
import java.util.Collection;

@Builder(toBuilder = true)
public record ReservationSpecifiers (
    Long listingId,
    String studentUsername,
    Long roomId,
    ReservationStatus status,
    Collection<ReservationStatus> statusList,
    ReservationType type,
    Boolean excludeCancelled
) {}
