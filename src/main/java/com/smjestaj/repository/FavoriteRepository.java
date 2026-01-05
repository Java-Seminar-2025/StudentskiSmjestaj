package com.smjestaj.repository;

import com.smjestaj.entity.FavoriteEntity;
import com.smjestaj.entity.ListingEntity;
import com.smjestaj.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {
    List<FavoriteEntity> findAllBySavedAndStudent(Boolean saved, UserEntity student);
    List<FavoriteEntity> findAllByStudentAndListing(UserEntity student, ListingEntity listing);
}
