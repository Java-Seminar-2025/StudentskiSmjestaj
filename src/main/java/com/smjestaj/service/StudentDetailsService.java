package com.smjestaj.service;

import com.smjestaj.dto.StudentData;
import com.smjestaj.mapper.FacultyMapper;
import com.smjestaj.mapper.StudentDetailsMapper;
import com.smjestaj.repository.FacultyRepository;
import com.smjestaj.repository.StudentDetailsRepository;
import com.smjestaj.repository.UserRepository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentDetailsService {
    private final UserRepository userRepository;

    private final StudentDetailsMapper studentDetailsMapper;
    private final StudentDetailsRepository studentDetailsRepository;

    private final FacultyRepository facultyRepository;
    private final FacultyMapper facultyMapper;

    public void addStudentDetails(StudentData studentData) {
        var studentDetails = studentDetailsMapper.studentDataToEntity(studentData);

        var student = userRepository.findById(studentData.studentId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        studentDetails.setStudent(student);

        if(facultyRepository.findByName(studentData.facultyName()).isEmpty()) {
            var faculty = facultyMapper.studentDataToFacultyEntity(studentData);
            facultyRepository.save(faculty);
            studentDetails.setFaculty(faculty);
            studentDetailsRepository.save(studentDetails);
            return;
        }

        var faculty = facultyRepository.findByName(studentData.facultyName()).
                orElseThrow();
        studentDetails.setFaculty(faculty);
        studentDetailsRepository.save(studentDetails);
    }
}
