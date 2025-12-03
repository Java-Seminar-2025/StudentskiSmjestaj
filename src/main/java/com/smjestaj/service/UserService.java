package com.smjestaj.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smjestaj.entity.UserEntity;
import com.smjestaj.repository.UserRepository;
import com.smjestaj.dto.*;
import com.smjestaj.exception.*;
import com.smjestaj.mapper.UserMapper;
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

    public void register(RegisterData input) {
        userValidator.validateEmailFormat(input.getEmail());
        userValidator.validatePassword(input.getPassword(), input.getConfirmPassword());

        if(userRepository.existsByEmail(input.getEmail())) {
            throw new RegisterException("Email already exists!");
        }

        if(userRepository.existsByUsername(input.getUsername())) {
            throw new RegisterException("Username already exists!");
        }

        UserEntity user = userMapper.registerDataToUserEntity(input);
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setBlocked(false);

        userRepository.save(user);
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
        UserEntity user = userRepository.findByUsername(changeRoleDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        user.setRole(changeRoleDto.getRole());
        userRepository.save(user);
    }

    public void blockOrUnblockUser(BlockOrUnblockDto blockOrUnblockDto) {
        UserEntity user = userRepository.findByUsername(blockOrUnblockDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        user.setBlocked(!blockOrUnblockDto.getBlocked());
        userRepository.save(user);
    }
}
