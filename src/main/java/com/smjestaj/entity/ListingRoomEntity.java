package com.smjestaj.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "listing_rooms")
@Getter
@Setter
@NoArgsConstructor
public class ListingRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private ListingEntity listing;

    @Column(name = "room_price", nullable = false)
    private Double roomPrice;

    @Column(nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<ReservationEntity> reservations;
}
