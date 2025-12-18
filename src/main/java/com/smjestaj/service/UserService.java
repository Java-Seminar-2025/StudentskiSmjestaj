package com.smjestaj.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smjestaj.entity.UserEntity;
import com.smjestaj.repository.*;
import com.smjestaj.dto.*;
import com.smjestaj.exception.*;
import com.smjestaj.mapper.*;
import com.smjestaj.enums.UserRole;

import lombok.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final FacultyMapper facultyMapper;
    private final FacultyRepository facultyRepository;
    private final StudentDetailsMapper studentDetailsMapper;
    private final StudentDetailsRepository studentDetailsRepository;

    public void register(RegisterData input) {
        userValidator.validateEmailFormat(input.email());
        userValidator.validatePassword(input.password(), input.confirmPassword());

        if(userRepository.existsByEmail(input.email())) {
            throw new RegisterException("Email already exists!");
        }

        if(userRepository.existsByUsername(input.username())) {
            throw new RegisterException("Username already exists!");
        }

        var user = userMapper.registerDataToUserEntity(input);
        user.setPassword(passwordEncoder.encode(input.password()));
        user.setBlocked(false);

        userRepository.save(user);
    }

    public String redirectToCorrectPage(String username, UserRole role) {
        if(role.getDisplayName().equals("student")) {
            var student = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
            return "redirect:/user/studentDetails?studentId=" + student.getId();
        }
        return "redirect:/user/login";
    }

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

    public List<SafeUserData> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::userEntityToSafeUserData)
                .collect(Collectors.toList());
    }

    public List<SafeUserData> getAllUsersWithRole(UserRole role) {
        List<UserEntity> usersWithRole = userRepository.findAllByRole(role);
        return usersWithRole.stream()
                .map(userMapper::userEntityToSafeUserData)
                .collect(Collectors.toList());
    }

    public void changeUserRole(ChangeRoleDto changeRoleDto) {
        UserEntity user = userRepository.findByUsername(changeRoleDto.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        user.setRole(changeRoleDto.role());
        userRepository.save(user);
    }

    public void blockOrUnblockUser(BlockOrUnblockDto blockOrUnblockDto) {
        UserEntity user = userRepository.findByUsername(blockOrUnblockDto.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        user.setBlocked(!blockOrUnblockDto.blocked());
        userRepository.save(user);
    }
}
