package com.smjestaj.repository;

import com.smjestaj.entity.ListingRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<ListingRoomEntity, Long> {
}
