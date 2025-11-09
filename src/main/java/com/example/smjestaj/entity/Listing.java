package com.example.smjestaj.entity;

import com.example.smjestaj.enums.UserGender;
import com.example.smjestaj.enums.ListingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "listings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listing_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "landlord_id", nullable = false)
    private User landlord;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "number_of_students")
    private Integer numberOfStudents;

    @Column(name = "number_of_rooms")
    private Integer numberOfRooms;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_gender")
    private UserGender preferredGender;

    @Enumerated(EnumType.STRING)
    private ListingStatus status;

    @Column(name = "cancellation_deadline")
    private LocalDateTime cancellationDeadline;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<ListingRoom> listingRooms;
}

