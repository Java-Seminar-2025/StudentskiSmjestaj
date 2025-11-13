package com.example.smjestaj.entity;

import com.example.smjestaj.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "phone_number")
    private Integer phoneNumber;

    @OneToMany(mappedBy = "landlord", cascade = CascadeType.ALL)
    private List<ListingEntity> listings;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<FavoriteEntity> favorites;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<ReservationEntity> reservations;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<RoomOccupantEntity> roomOccupants;
}

