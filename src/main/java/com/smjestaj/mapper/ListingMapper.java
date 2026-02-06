package com.smjestaj.mapper;

import org.mapstruct.*;

import com.smjestaj.dto.MyReservationData;
import com.smjestaj.entity.ListingEntity;
import com.smjestaj.dto.ListingData;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ListingMapper {
    @Mapping(target = "listingId", source = "id")
    @Mapping(target = "landlordUsername", expression = "java(listingEntity.getLandlord().getUsername())")
    ListingData listingEntityToDto(ListingEntity listingEntity);

    @Mapping(target = "cancellationDeadline", ignore = true)
    ListingEntity listingDtoToEntity(ListingData listingData);

    @Mapping(target = "cancellationDeadline", ignore = true)
    void updateEntityFromDto(ListingData listingData, @MappingTarget ListingEntity listingEntity);

    @Mapping(target = "listingId", source = "id")
    @Mapping(target = "landlordUsername", expression = "java(listingEntity.getLandlord().getUsername())")
    @Mapping(target = "status", ignore = true)
    MyReservationData listingEntityToMyReservationData(ListingEntity listingEntity);
}

