package com.smjestaj.mapper;

import org.mapstruct.*;
import com.smjestaj.entity.ListingEntity;
import com.smjestaj.entity.UserEntity;
import com.smjestaj.dto.ListingData;

@Mapper(componentModel = "spring")
public interface ListingMapper {
    @Mapping(target = "listingId", source = "id")
    @Mapping(target = "landlordUsername", source = "landlord.username")
    ListingData listingEntityToDto(ListingEntity listingEntity);

    @Mapping(target = "landlord", source = "landlordUsername")
    ListingEntity listingDtoToEntity(ListingData listingData);

    default UserEntity map(String landlordUsername) {
        if (landlordUsername == null) {
            return null;
        }
        UserEntity user = new UserEntity();
        user.setUsername(landlordUsername);
        return user;
    }
}

