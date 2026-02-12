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

    public void register(RegisterData registerData) {
        userValidator.validateEmailFormat(registerData.email());
        userValidator.validatePassword(registerData.password(), registerData.confirmPassword());

        if(userRepository.existsByEmail(registerData.email())) {
            throw new RegisterException("Email already exists!");
        }

        if(userRepository.existsByUsername(registerData.username())) {
            throw new RegisterException("Username already exists!");
        }

        var user = userMapper.registerDataToUserEntity(registerData);
        user.setPassword(passwordEncoder.encode(registerData.password()));
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

    public SafeUserData getUserData(String username) {
        var user = userRepository.findByUsername(username).orElseThrow();
        return userMapper.userEntityToSafeUserData(user);
    }

    public List<SafeUserData> getAllUsers() {
        var users = userRepository.findAllByOrderByIdAsc();
        return users.stream()
                .map(userMapper::userEntityToSafeUserData)
                .toList();
    }
}
