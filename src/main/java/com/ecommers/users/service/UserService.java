package com.ecommers.users.service;

import com.ecommers.users.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto.UserResponse getUserById(Long id);
    List<UserDto.UserResponse> getAllUsers();
    UserDto.UserResponse createUser(UserDto.UserRequest request);
    UserDto.UserResponse updateUser(Long id, UserDto.UserRequest request);
    void deleteUser(Long id);
}