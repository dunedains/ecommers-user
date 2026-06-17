package com.ecommers.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserDto {

    public record UserRequest(
            @NotBlank(message = "El nombre es obligatorio") String name,
            @NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") String email,
            @NotBlank(message = "La dirección es obligatoria") String address,
            @NotBlank(message = "La contraseña es obligatoria") String password,
            String role
    ) {}

    public record LoginRequest(
            @NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") String email,
            @NotBlank(message = "La contraseña es obligatoria") String password
    ) {}

    public record UserResponse(Long id, String name, String email, String address, String role) {}
}
