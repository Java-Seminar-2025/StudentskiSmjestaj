package com.smjestaj.repository;

import com.smjestaj.entity.ListingEntity;
import com.smjestaj.entity.ListingRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<ListingRoomEntity, Long> {
    List<ListingRoomEntity> findAllByListing(ListingEntity listingEntity);
}
