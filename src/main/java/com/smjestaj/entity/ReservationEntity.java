package com.smjestaj.entity;

import com.smjestaj.enums.ReservationStatus;
import com.smjestaj.enums.ReservationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private ListingRoomEntity room;

    @ManyToOne(optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private ListingEntity listing;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "cancellation_deadline")
    private LocalDateTime cancellationDeadline;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Enumerated(EnumType.STRING)
    private ReservationType type;

    public static ReservationEntity createRoomReservation(UserEntity student, ListingRoomEntity room) {
        var reservation = new ReservationEntity();
        reservation.student = student;
        reservation.room = room;
        reservation.listing = room.getListing();
        reservation.status = ReservationStatus.PENDING;
        reservation.createdAt = LocalDateTime.now();
        reservation.type = ReservationType.ROOM;
        return reservation;
    }

    public static ReservationEntity createFullListingReservation(UserEntity student, ListingEntity listing) {
        var reservation = new ReservationEntity();
        reservation.student = student;
        reservation.listing = listing;
        reservation.status = ReservationStatus.PENDING;
        reservation.createdAt = LocalDateTime.now();
        reservation.type = ReservationType.FULL_LISTING;
        return reservation;
    }
}
