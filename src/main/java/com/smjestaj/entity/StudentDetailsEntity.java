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
    @Column(name = "student_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "student_id")
    private UserEntity student;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private FacultyEntity faculty;

    @Column(name = "year_of_study")
    private Integer yearOfStudy;

    @Enumerated(EnumType.STRING)
    private UserGender gender;
}
