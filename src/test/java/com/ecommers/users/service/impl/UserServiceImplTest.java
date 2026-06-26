package com.ecommers.users.service.impl;

import com.ecommers.users.dto.UserDto.UserRequest;
import com.ecommers.users.dto.UserDto.UserResponse;
import com.ecommers.users.dto.UserDto.LoginRequest;
import com.ecommers.users.exception.InvalidCredentialsException;
import com.ecommers.users.exception.UserNotFoundException;
import com.ecommers.users.model.User;
import com.ecommers.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de usuarios.
 * Se mockean el repositorio y el PasswordEncoder (BCrypt) para validar el
 * registro y la verificación de credenciales del login, sin base de datos.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    @DisplayName("createUser: hashea la contraseña, asigna rol USER y guarda el usuario")
    void createUser_hasheaPasswordYAsignaRolUser() {
        // Given: el encoder devuelve un hash y el repositorio asigna un id
        when(passwordEncoder.encode("secret123")).thenReturn("$2a$HASH");
        when(repository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        // When
        UserResponse response = service.createUser(
                new UserRequest("Juan", "juan@mail.com", "Calle 123", "secret123", null));

        // Then: rol por defecto USER y se hasheó la contraseña (nunca se guarda en claro)
        assertThat(response.role()).isEqualTo("USER");
        assertThat(response.email()).isEqualTo("juan@mail.com");
        verify(passwordEncoder).encode("secret123");
        verify(repository).save(any(User.class));
    }

    @Test
    @DisplayName("validateCredentials: con email y contraseña correctos devuelve el usuario")
    void validateCredentials_credencialesCorrectas_devuelveUsuario() {
        // Given: existe el usuario y la contraseña coincide con el hash
        User user = new User();
        user.setId(1L);
        user.setEmail("juan@mail.com");
        user.setPassword("$2a$HASH");
        user.setRole("USER");
        when(repository.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "$2a$HASH")).thenReturn(true);

        // When
        UserResponse response = service.validateCredentials(
                new LoginRequest("juan@mail.com", "secret123"));

        // Then
        assertThat(response.email()).isEqualTo("juan@mail.com");
        assertThat(response.role()).isEqualTo("USER");
    }

    @Test
    @DisplayName("validateCredentials: si el email no existe, lanza InvalidCredentialsException")
    void validateCredentials_emailInexistente_lanzaExcepcion() {
        // Given: no hay usuario con ese email
        when(repository.findByEmail("nadie@mail.com")).thenReturn(Optional.empty());

        // When / Then: no se llega siquiera a comparar la contraseña
        assertThatThrownBy(() -> service.validateCredentials(
                new LoginRequest("nadie@mail.com", "secret123")))
                .isInstanceOf(InvalidCredentialsException.class);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("validateCredentials: con contraseña incorrecta lanza InvalidCredentialsException")
    void validateCredentials_passwordIncorrecta_lanzaExcepcion() {
        // Given: el usuario existe pero la contraseña no coincide
        User user = new User();
        user.setEmail("juan@mail.com");
        user.setPassword("$2a$HASH");
        when(repository.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("malo", "$2a$HASH")).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> service.validateCredentials(
                new LoginRequest("juan@mail.com", "malo")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("getUserById: si el usuario no existe, lanza UserNotFoundException")
    void getUserById_inexistente_lanzaExcepcion() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("deleteUser: si el usuario no existe, no intenta borrar y lanza excepción")
    void deleteUser_inexistente_lanzaExcepcion() {
        // Given
        when(repository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> service.deleteUser(99L))
                .isInstanceOf(UserNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("getAllUsers: mapea todos los usuarios")
    void getAllUsers_devuelveLista() {
        User u = new User();
        u.setId(1L);
        u.setEmail("ana@mail.com");
        u.setRole("USER");
        when(repository.findAll()).thenReturn(java.util.List.of(u));

        assertThat(service.getAllUsers()).hasSize(1);
    }

    @Test
    @DisplayName("getUserById: devuelve el usuario existente")
    void getUserById_existente_devuelve() {
        User u = new User();
        u.setId(1L);
        u.setName("Ana");
        u.setEmail("ana@mail.com");
        u.setRole("USER");
        when(repository.findById(1L)).thenReturn(Optional.of(u));

        assertThat(service.getUserById(1L).email()).isEqualTo("ana@mail.com");
    }

    @Test
    @DisplayName("updateUser: actualiza datos y re-hashea la contraseña")
    void updateUser_existente_actualiza() {
        User u = new User();
        u.setId(1L);
        u.setRole("USER");
        when(repository.findById(1L)).thenReturn(Optional.of(u));
        when(passwordEncoder.encode("nueva123")).thenReturn("$2a$NEW");
        when(repository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        var response = service.updateUser(1L, new UserRequest("Ana", "ana@mail.com", "Calle 9", "nueva123", null));

        assertThat(response.name()).isEqualTo("Ana");
        verify(passwordEncoder).encode("nueva123");
    }

    @Test
    @DisplayName("deleteUser: borra un usuario existente")
    void deleteUser_existente_borra() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteUser(1L);

        verify(repository).deleteById(1L);
    }
}
