package com.ecommers.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserDto {

    public record UserRequest(
            @NotBlank
            String name,
            @NotBlank
            @Email
            String email,
            @NotBlank
            String direccion) {
    }

    public record UserResponse(
            Long id,
            String name,
            String email,
            String direccion) {
    }
}