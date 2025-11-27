package com.smjestaj.mapper;

import org.mapstruct.*;
import com.smjestaj.entity.UserEntity;
import com.smjestaj.dto.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity registerDataToUserEntity(RegisterData input);

    SafeUserData userEntityToSafeUserData(UserEntity user);
}
