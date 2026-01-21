package com.smjestaj.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoomData {
    private Long roomId;
    private Double roomPrice;
    private Integer capacity;
}