package com.smjestaj.mapper;

import com.smjestaj.dto.ReservationData;
import com.smjestaj.entity.ReservationEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "id", source = "reservationId")
    @Mapping(target = "listing.id", source = "listingId")
    @Mapping(target = "room.id", source = "roomId")
    @Mapping(target = "student.username", source = "studentUsername")
    ReservationEntity reservationDtoToEntity(ReservationData reservationData);

    @Mapping(target = "reservationId", source = "id")
    @Mapping(target = "studentUsername", source = "student.username")
    @Mapping(target = "roomId", source = "room.id")
    ReservationData reservationEntityToDto(ReservationEntity reservationEntity);
}
