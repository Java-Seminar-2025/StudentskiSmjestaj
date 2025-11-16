package com.smjestaj.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.smjestaj.repository.UserRepository;
import com.smjestaj.entity.UserEntity;
import com.smjestaj.dto.*;
import com.smjestaj.exception.*;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void register(RegisterData input) {
        if(userRepository.existsByEmail(input.getEmail())) {
            throw new RegisterException("Email already exists!");
        }
        if(!input.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RegisterException("Invalid email format!");
        }
        if(input.getPassword().equals(input.getConfirmPassword())) {
            throw new RegisterException("Passwords do not match!");
        }

        var user = new UserEntity();
        user.setName(input.getName());
        user.setSurname(input.getSurname());
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole(input.getRole());

        userRepository.save(user);
    }

    public SafeUserData login(LoginData input) {
        var user = userRepository.findByUsername(input.getUsername()).
                orElseThrow(() -> new LoginFailedException("Wrong username and/or password!"));

        if(!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new LoginFailedException("Wrong username and/or password!");
        }

        return new SafeUserData(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.getPhoneNumber()
        );
    }
}
