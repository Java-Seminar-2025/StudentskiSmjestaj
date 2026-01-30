package com.smjestaj.repository;

import com.smjestaj.entity.StudentDetailsEntity;
import com.smjestaj.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentDetailsRepository extends JpaRepository<StudentDetailsEntity, Long> {
    Optional<StudentDetailsEntity> findByStudent(UserEntity student);
}
