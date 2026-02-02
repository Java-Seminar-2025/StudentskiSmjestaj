package com.smjestaj.repository;

import com.smjestaj.entity.ReservationEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long>, JpaSpecificationExecutor<ReservationEntity>
{
    @Query("""
    select distinct r.listing.id
    from ReservationEntity r
    where r.student.username = :username
      and r.status <> com.smjestaj.enums.ReservationStatus.CANCELLED
    """)
    List<Long> findListingIdsWithReservationsForStudent(@Param("username") String username);
}
