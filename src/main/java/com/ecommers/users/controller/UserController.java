package com.ecommers.users.controller;

import com.ecommers.users.dto.UserDto;
import com.ecommers.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto.UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto.UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto.UserResponse> createUser(@Valid @RequestBody UserDto.UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto.UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto.UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Validacion de credenciales para uso interno del API Gateway.
     * Devuelve los datos del usuario si email + password son correctos, 401 en caso contrario.
     */
    @PostMapping("/validate")
    public ResponseEntity<UserDto.UserResponse> validate(@Valid @RequestBody UserDto.LoginRequest request) {
        return ResponseEntity.ok(userService.validateCredentials(request));
    }
}
