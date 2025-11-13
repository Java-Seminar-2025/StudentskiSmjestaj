package com.example.smjestaj.entity;

import com.example.smjestaj.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private ListingRoomEntity room;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
}
