package com.smjestaj.dto;

import lombok.*;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class RoomData {
    private Long roomId;
    private Double roomPrice;
    private Integer capacity;
    private List<ReservationData> reservations;
    private Boolean isReservable;
}