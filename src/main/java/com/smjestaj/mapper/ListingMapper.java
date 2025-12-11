package com.smjestaj.mapper;

import org.mapstruct.*;
import com.smjestaj.entity.ListingEntity;
import com.smjestaj.dto.ListingData;

@Mapper(componentModel = "spring")
public interface ListingMapper {
    @Mapping(target = "listingId", source = "id")
    @Mapping(target = "landlordUsername", expression = "java(listingEntity.getLandlord().getUsername())")
    ListingData listingEntityToDto(ListingEntity listingEntity);

    ListingEntity listingDtoToEntity(ListingData listingData);
}

