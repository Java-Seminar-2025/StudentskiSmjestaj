package com.smjestaj.repository;

import com.smjestaj.entity.ListingEntity;
import com.smjestaj.entity.ListingRoomEntity;
import com.smjestaj.entity.ReservationEntity;
import com.smjestaj.entity.UserEntity;
import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findAllByRoomAndStatus(ListingRoomEntity room, ReservationStatus status);
    List<ReservationEntity> findAllByListingAndStatusAndType(ListingEntity listing, ReservationStatus status, ReservationType type);
    List<ReservationEntity> findAllByListingAndStudentAndType(ListingEntity listing, UserEntity student, ReservationType type);
}
