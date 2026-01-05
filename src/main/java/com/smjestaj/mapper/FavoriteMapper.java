package com.smjestaj.mapper;

import com.smjestaj.dto.FavoriteData;
import com.smjestaj.entity.FavoriteEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {
    @Mapping(target = "listingId", expression = "java(favoriteEntity.getListing().getId())")
    @Mapping(target = "studentId", expression = "java(favoriteEntity.getStudent().getId())")
    FavoriteData favoriteEntityToDto(FavoriteEntity favoriteEntity);
}
