package com.ecommers.users.service.impl;

import lombok.extern.slf4j.Slf4j;
import com.ecommers.users.dto.UserDto;
import com.ecommers.users.exception.InvalidCredentialsException;
import com.ecommers.users.exception.UserNotFoundException;
import com.ecommers.users.model.User;
import com.ecommers.users.repository.UserRepository;
import com.ecommers.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto.UserResponse getUserById(Long id) {
        log.info("Buscando usuario id={}", id);
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<UserDto.UserResponse> getAllUsers() {
        log.info("Listando todos los usuarios");
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public UserDto.UserResponse createUser(UserDto.UserRequest request) {
        log.info("Creando usuario email={}", request.email());
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setAddress(request.address());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(normalizeRole(request.role()));
        return toResponse(repository.save(user));
    }

    @Override
    @Transactional
    public UserDto.UserResponse updateUser(Long id, UserDto.UserRequest request) {
        log.info("Actualizando usuario id={}", id);
        User user = findOrThrow(id);
        user.setName(request.name());
        user.setEmail(request.email());
        user.setAddress(request.address());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(normalizeRole(request.role()));
        return toResponse(repository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Eliminando usuario id={}", id);
        if (!repository.existsById(id)) throw new UserNotFoundException(id);
        repository.deleteById(id);
    }

    @Override
    public UserDto.UserResponse validateCredentials(UserDto.LoginRequest request) {
        log.info("Validando credenciales email={}", request.email());
        User user = repository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);
        if (user.getPassword() == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return toResponse(user);
    }

    private User findOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    /** Solo se aceptan los roles USER y ADMIN; por defecto USER. */
    private String normalizeRole(String role) {
        if (role != null && role.equalsIgnoreCase("ADMIN")) {
            return "ADMIN";
        }
        return "USER";
    }

    private UserDto.UserResponse toResponse(User u) {
        return new UserDto.UserResponse(u.getId(), u.getName(), u.getEmail(), u.getAddress(), u.getRole());
    }
}
