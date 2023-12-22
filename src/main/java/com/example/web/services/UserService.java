package com.example.web.services;

import com.example.web.dto.RegistrationDto;
import com.example.web.models.UserEntity;

public interface UserService {
    void saveUser(RegistrationDto registrationDto);
    void deleteUser(Long userId);
    UserEntity findByEmail(String email);

    UserEntity findByUsername(String username);

    void updateUser(RegistrationDto registrationDto);

    long countOfAllUsers();

    UserEntity findById(Long userID);
}
