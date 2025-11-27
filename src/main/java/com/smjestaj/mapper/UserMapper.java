package com.smjestaj.mapper;

import org.mapstruct.*;
import com.smjestaj.entity.UserEntity;
import com.smjestaj.dto.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "listings", ignore = true),
            @Mapping(target = "favorites", ignore = true),
            @Mapping(target = "reservations", ignore = true),
            @Mapping(target = "roomOccupants", ignore = true)
    })
    UserEntity registerDataToUserEntity(RegisterData input);

    SafeUserData userEntityToSafeUserData(UserEntity user);
}

