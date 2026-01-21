package com.smjestaj.mapper;

import com.smjestaj.dto.ReservationData;
import com.smjestaj.entity.ReservationEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationEntity reservationDtoToEntity(ReservationData reservationData);
}
