package com.example.smjestaj.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.smjestaj.repository.UserRepository;
import com.example.smjestaj.entity.UserEntity;
import com.example.smjestaj.enums.UserRole;
import com.example.smjestaj.exception.EmailAlreadyExistsException;
import com.example.smjestaj.exception.InvalidEmailFormatException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity register(String name, String email, String password, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidEmailFormatException("Invalid email format!");
        }

        UserEntity user = new UserEntity();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        return userRepository.save(user);
    }
}
