package com.smjestaj.dto;

import lombok.*;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class AllRoomsData {
    private final Long listingId;
    private final Integer numberOfRooms;
    private List<RoomData> rooms;
}
