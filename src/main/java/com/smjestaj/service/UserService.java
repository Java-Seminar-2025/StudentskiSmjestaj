package com.smjestaj.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smjestaj.repository.*;
import com.smjestaj.dto.*;
import com.smjestaj.exception.*;
import com.smjestaj.mapper.*;
import com.smjestaj.enums.UserRole;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

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

    public List<SafeUserData> getAllUsers() {
        var users = userRepository.findAllByOrderByIdAsc();
        return users.stream()
                .map(userMapper::userEntityToSafeUserData)
                .toList();
    }

    public List<SafeUserData> getAllUsersWithRole(UserRole role) {
        var usersWithRole = userRepository.findAllByRole(role);
        return usersWithRole.stream()
                .map(userMapper::userEntityToSafeUserData)
                .toList();
    }

    public void changeUserRole(ChangeRoleDto changeRoleDto) {
        var user = userRepository.findByUsername(changeRoleDto.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        user.setRole(changeRoleDto.role());
        userRepository.save(user);
    }

    public void blockOrUnblockUser(BlockOrUnblockDto blockOrUnblockDto) {
        var user = userRepository.findByUsername(blockOrUnblockDto.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        user.setBlocked(!blockOrUnblockDto.blocked());
        userRepository.save(user);
    }
}
