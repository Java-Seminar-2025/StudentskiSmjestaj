package com.smjestaj.mapper;

import com.smjestaj.dto.StudentData;
import com.smjestaj.entity.FacultyEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FacultyMapper {
    @Mapping(target = "name", source = "facultyName")
    @Mapping(target = "address", source = "facultyAddress")
    @Mapping(target = "city", source = "facultyCity")
    FacultyEntity studentDataToFacultyEntity(StudentData studentData);
}
