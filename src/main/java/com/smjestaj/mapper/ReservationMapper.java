package com.smjestaj.mapper;

import com.smjestaj.dto.ReservationData;
import com.smjestaj.entity.ReservationEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationEntity reservationDtoToEntity(ReservationData reservationData);

    @Mapping(target = "reservationId", source = "id")
    @Mapping(target = "studentUsername", source = "student.username")
    ReservationData reservationEntityToDto(ReservationEntity reservationEntity);
}
