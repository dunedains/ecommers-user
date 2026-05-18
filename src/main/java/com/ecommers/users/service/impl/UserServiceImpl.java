package com.ecommers.users.service.impl;

import com.ecommers.users.dto.UserDto;
import com.ecommers.users.exception.UserNotFoundException;
import com.ecommers.users.model.User;
import com.ecommers.users.repository.UserRepository;
import com.ecommers.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto.UserResponse getUserById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public List<UserDto.UserResponse> getAllUsers() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserDto.UserResponse createUser(UserDto.UserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setDireccion(request.direccion());
        return toResponse(repository.save(user));
    }

    @Override
    @Transactional
    public UserDto.UserResponse updateUser(Long id, UserDto.UserRequest request) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setName(request.name());
        user.setEmail(request.email());
        user.setDireccion(request.direccion());
        return toResponse(repository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private UserDto.UserResponse toResponse(User user) {
        return new UserDto.UserResponse(user.getId(), user.getName(), user.getEmail(), user.getDireccion());
    }
}
