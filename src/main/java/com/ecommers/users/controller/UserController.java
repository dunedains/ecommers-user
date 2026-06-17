package com.ecommers.users.controller;

import com.ecommers.users.dto.UserDto;
import com.ecommers.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserDto.UserResponse>>> getAllUsers() {
        List<EntityModel<UserDto.UserResponse>> users = userService.getAllUsers().stream()
                .map(this::toModel)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto.UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(toModel(userService.getUserById(id)));
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserDto.UserResponse>> createUser(@Valid @RequestBody UserDto.UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(userService.createUser(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto.UserResponse>> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto.UserRequest request) {
        return ResponseEntity.ok(toModel(userService.updateUser(id, request)));
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

    private EntityModel<UserDto.UserResponse> toModel(UserDto.UserResponse user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(user.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
    }
}
