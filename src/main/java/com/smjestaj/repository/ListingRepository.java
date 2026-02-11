package com.smjestaj.repository;

import com.smjestaj.entity.ListingEntity;
import com.smjestaj.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import org.springframework.data.domain.*;

public interface ListingRepository extends JpaRepository<ListingEntity, Long>, JpaSpecificationExecutor<ListingEntity> {
    List<ListingEntity> findAllByLandlordAndDeleted(UserEntity landlord, Boolean deleted);
    Page<ListingEntity> findAllByLandlordAndDeleted(UserEntity landlord, Boolean deleted, Pageable pageable);
    List<ListingEntity> findTop3ByDeletedOrderByIdDesc(Boolean deleted);
    List<ListingEntity> findAllByIdIn(List<Long> ids);
}
