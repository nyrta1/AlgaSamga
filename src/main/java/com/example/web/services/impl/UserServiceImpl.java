package com.example.web.services.impl;

import com.example.web.dto.RegistrationDto;
import com.example.web.models.UserEntity;
import com.example.web.repository.RoleRepository;
import com.example.web.repository.UserRepository;
import com.example.web.security.SecurityUtil;
import com.example.web.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(RegistrationDto registrationDto) {
        UserEntity user = new UserEntity();
        user.setSurname(registrationDto.getSurname());
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setRoles(Arrays.asList(roleRepository.findByName("STUDENT")));
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setPhotoUrl(registrationDto.getPhotoUrl());
        user.setUsername(registrationDto.getUsername());

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        // Remove user-role references
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")); // Adjust the exception type as needed

        user.getRoles().clear();
        userRepository.save(user);

        // Remove role references from "users_roles" table
        roleRepository.findById(userId).ifPresent(role -> role.getUsers().remove(user));

        // Delete the role
        roleRepository.deleteById(userId);

        // Delete the user
        userRepository.deleteById(userId);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void updateUser(RegistrationDto registrationDto) {
        UserEntity user = new UserEntity();
        user.setId(registrationDto.getId());
        user.setSurname(registrationDto.getSurname());
        user.setName(registrationDto.getName());
        user.setPhotoUrl(registrationDto.getPhotoUrl());
        user.setEmail(registrationDto.getEmail());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setUsername(registrationDto.getUsername());
        user.setPassword(registrationDto.getPassword());
        user.setRoles(
                (userRepository.findByUsername(SecurityUtil.getSessionUser()))
                        .getRoles()
        );
        userRepository.save(user);
    }

    @Override
    public long countOfAllUsers() {
        return userRepository.count();
    }

    @Override
    public UserEntity findById(Long userID) {
        return userRepository.findById(userID).orElse(null);
    }
}
