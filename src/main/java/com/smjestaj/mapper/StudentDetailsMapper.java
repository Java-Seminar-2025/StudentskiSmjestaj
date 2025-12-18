package com.smjestaj.mapper;

import com.smjestaj.dto.StudentData;
import com.smjestaj.entity.StudentDetailsEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StudentDetailsMapper {
    StudentDetailsEntity studentDataToEntity(StudentData studentData);
}
