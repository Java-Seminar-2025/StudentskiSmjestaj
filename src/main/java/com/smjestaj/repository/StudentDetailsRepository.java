package com.smjestaj.repository;

import com.smjestaj.entity.StudentDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentDetailsRepository extends JpaRepository<StudentDetailsEntity, Long> {
}
