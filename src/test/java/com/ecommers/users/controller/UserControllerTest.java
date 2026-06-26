package com.ecommers.users.controller;

import com.ecommers.users.dto.UserDto.UserResponse;
import com.ecommers.users.exception.InvalidCredentialsException;
import com.ecommers.users.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService service;

    private UserResponse sample() {
        return new UserResponse(1L, "Ana", "ana@mail.com", "Calle 1", "USER");
    }

    @Test
    @DisplayName("GET /api/v1/users -> 200")
    void getAll_devuelve200() throws Exception {
        when(service.getAllUsers()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ana@mail.com"));
    }

    @Test
    @DisplayName("POST /api/v1/users válido -> 201")
    void create_devuelve201() throws Exception {
        when(service.createUser(any())).thenReturn(sample());

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ana\",\"email\":\"ana@mail.com\",\"address\":\"Calle 1\",\"password\":\"secret123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("POST /api/v1/users sin email -> 400")
    void create_invalido_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ana\",\"address\":\"Calle 1\",\"password\":\"secret123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/users/validate credenciales OK -> 200")
    void validate_ok_devuelve200() throws Exception {
        when(service.validateCredentials(any())).thenReturn(sample());

        mockMvc.perform(post("/api/v1/users/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@mail.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/users/validate credenciales inválidas -> 401")
    void validate_invalido_devuelve401() throws Exception {
        when(service.validateCredentials(any())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/users/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@mail.com\",\"password\":\"mala\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} -> 204")
    void delete_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }
}
