package com.smjestaj.repository;

import com.smjestaj.entity.FacultyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<FacultyEntity, Long> {
    Optional<FacultyEntity> findByName(String name);
}
