package com.smjestaj.entity;

import com.smjestaj.enums.UserGender;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_details")
@Getter
@Setter
@NoArgsConstructor
public class StudentDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private FacultyEntity faculty;

    @Column(name = "year_of_study")
    private Integer yearOfStudy;

    @Enumerated(EnumType.STRING)
    private UserGender gender;
}
