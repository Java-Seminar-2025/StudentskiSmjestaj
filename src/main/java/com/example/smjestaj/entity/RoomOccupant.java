package com.example.smjestaj.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_occupants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomOccupant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private ListingRoom room;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
}
