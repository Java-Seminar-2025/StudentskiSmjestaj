package com.smjestaj.dto;

import lombok.*;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class AllRoomsData {
    private final Long listingId;
    private List<RoomData> rooms;
}
