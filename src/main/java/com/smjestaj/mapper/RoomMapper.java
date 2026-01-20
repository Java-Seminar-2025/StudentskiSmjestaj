package com.smjestaj.mapper;

import com.smjestaj.dto.RoomData;
import com.smjestaj.entity.ListingRoomEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    ListingRoomEntity roomDtoToEntity(RoomData roomData);
    RoomData roomEntityToDto(ListingRoomEntity roomEntity);
}
