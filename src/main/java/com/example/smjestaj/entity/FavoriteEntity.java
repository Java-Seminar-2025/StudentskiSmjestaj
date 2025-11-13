package com.example.smjestaj.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorites")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_listing_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private ListingEntity listing;
}
