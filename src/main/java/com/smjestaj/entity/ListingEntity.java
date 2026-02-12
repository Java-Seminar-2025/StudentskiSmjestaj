package com.smjestaj.entity;

import com.smjestaj.enums.UserGender;
import com.smjestaj.enums.ListingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "listings")
@Getter
@Setter
@NoArgsConstructor
public class ListingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listing_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "landlord_id", nullable = false)
    private UserEntity landlord;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Double price;

    @Column(name = "number_of_students", nullable = false)
    private Integer numberOfStudents;

    @Column(name = "number_of_rooms", nullable = false)
    private Integer numberOfRooms;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_gender")
    private UserGender preferredGender;

    @Enumerated(EnumType.STRING)
    private ListingStatus status;

    @Column(name = "days_to_cancel")
    private Integer daysToCancel;

    @Column(name = "is_deleted")
    private Boolean deleted;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<FavoriteEntity> favorites;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<ListingRoomEntity> listingRooms;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<ReservationEntity> reservations;

    public Optional<String> updateDaysToCancel(Integer daysToCancel) {
        if (daysToCancel == null) {
            return Optional.empty();
        }
        if (daysToCancel < this.daysToCancel) {
            return Optional.of("It's not allowed to shorten the cancellation deadline.");
        }
        this.setDaysToCancel(daysToCancel);
        return Optional.empty();
    }
}

