package com.example.smjestaj.entity;

import com.example.smjestaj.enums.UserGender;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetails {

    @Id
    @Column(name = "student_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(name = "year_of_study")
    private Integer yearOfStudy;

    @Enumerated(EnumType.STRING)
    private UserGender gender;
}
