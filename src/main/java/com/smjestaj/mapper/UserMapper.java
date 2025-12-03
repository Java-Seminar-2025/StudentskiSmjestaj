package com.smjestaj.mapper;

import org.mapstruct.*;
import com.smjestaj.entity.UserEntity;
import com.smjestaj.dto.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    UserEntity registerDataToUserEntity(RegisterData input);

    @Mapping(target = "blocked", source = "blocked")
    SafeUserData userEntityToSafeUserData(UserEntity user);
}

