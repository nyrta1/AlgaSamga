package com.example.web.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegistrationDto {
    private Long id;
    @NotEmpty(message = "Surname should not be empty")
    private String surname;
    @NotEmpty(message = "Name should not be empty")
    private String name;
    private String phoneNumber;
    private String photoUrl;
    @NotEmpty(message = "Email should not be empty")
    private String email;
    @NotEmpty(message = "Password should not be empty")
    private String password;
    @NotEmpty(message = "Username should not be empty")
    private String username;
}
