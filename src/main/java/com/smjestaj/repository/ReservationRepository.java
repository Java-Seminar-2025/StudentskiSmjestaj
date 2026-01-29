package com.smjestaj.repository;

import com.smjestaj.entity.ListingEntity;
import com.smjestaj.entity.ListingRoomEntity;
import com.smjestaj.entity.ReservationEntity;
import com.smjestaj.entity.UserEntity;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long>,
        JpaSpecificationExecutor<ReservationEntity> {
    /*
    List<ReservationEntity> findAllByRoomAndStatus(ListingRoomEntity room, ReservationStatus status);
    List<ReservationEntity> findAllByListingAndStatus(ListingEntity listing, ReservationStatus status);
    List<ReservationEntity> findAllByListingAndStatusAndType(ListingEntity listing, ReservationStatus status, ReservationType type);
    List<ReservationEntity> findAllByListingAndStudentAndStatusAndType(ListingEntity listing, UserEntity student, ReservationStatus status, ReservationType type);
    List<ReservationEntity> findAllByListingAndStudentAndType(ListingEntity listing, UserEntity student, ReservationType type);
    */

    List<ReservationEntity> findAllByStudent(UserEntity student);
    Page<ReservationEntity> findAllByStudent(UserEntity student, Pageable pageable);
}
