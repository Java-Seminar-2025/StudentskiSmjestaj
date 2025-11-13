package com.example.smjestaj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.smjestaj.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
