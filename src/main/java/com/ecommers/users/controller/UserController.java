package com.ecommers.users.controller;

import com.ecommers.users.dto.UserDto;
import com.ecommers.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios. Las escrituras requieren rol ADMIN (validado en el API Gateway); "
        + "el registro público de clientes se hace vía /auth/register del gateway")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios")
    public ResponseEntity<List<UserDto.UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"id\":1,\"name\":\"Juan\",\"email\":\"juan@mail.com\",\"address\":\"Calle 123\",\"role\":\"USER\"}"))),
            @ApiResponse(responseCode = "404", description = "El usuario no existe")
    })
    public ResponseEntity<UserDto.UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @Operation(summary = "Crear un usuario",
            description = "Permite indicar el rol (USER o ADMIN); cualquier otro valor se normaliza a USER. "
                    + "Solo un ADMIN puede llegar a este endpoint a través del gateway.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    public ResponseEntity<UserDto.UserResponse> createUser(@Valid @RequestBody UserDto.UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "El usuario no existe")
    })
    public ResponseEntity<UserDto.UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto.UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "El usuario no existe")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Validacion de credenciales para uso interno del API Gateway.
     * Devuelve los datos del usuario si email + password son correctos, 401 en caso contrario.
     */
    @PostMapping("/validate")
    @Operation(summary = "Validar credenciales (uso interno del gateway)",
            description = "Usado por /auth/login del gateway para emitir el JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credenciales válidas"),
            @ApiResponse(responseCode = "401", description = "Email o contraseña incorrectos")
    })
    public ResponseEntity<UserDto.UserResponse> validate(@Valid @RequestBody UserDto.LoginRequest request) {
        return ResponseEntity.ok(userService.validateCredentials(request));
    }
}
