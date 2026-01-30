package com.smjestaj.repository;

import com.smjestaj.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long>, JpaSpecificationExecutor<ReservationEntity>
{}
