package com.smjestaj.service;

import com.smjestaj.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.smjestaj.repository.UserRepository;
import com.smjestaj.entity.UserEntity;
import com.smjestaj.dto.*;
import com.smjestaj.exception.*;

import lombok.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    public void register(RegisterData input) {
        userValidator.validateEmail(input.getEmail());
        userValidator.validatePassword(input.getPassword(), input.getConfirmPassword());
        userValidator.validateUsername(input.getUsername());

        UserEntity user = userMapper.registerDataToUserEntity(input);

        userRepository.save(user);
    }

    public SafeUserData login(LoginData input) {
        var user = userRepository.findByUsername(input.getUsername()).
                orElseThrow(() -> new LoginFailedException("Wrong username and/or password!"));

        if(!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new LoginFailedException("Wrong username and/or password!");
        }

        return userMapper.userEntityToSafeUserData(user);
    }
}
