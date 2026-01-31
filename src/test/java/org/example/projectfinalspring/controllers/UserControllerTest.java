package org.example.projectfinalspring.controllers;

import org.example.projectfinalspring.entities.User;
import org.example.projectfinalspring.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private UserService userService;

    private final Long EXISTING_USER_ID = 1L;

    private final String USER_ENDPOINT = "/api/users";


    @Test
    void getUserById_shouldReturnUser() throws Exception {
        // Подготавливаем тестового пользователя
        User mockUser = new User();
        mockUser.setId(EXISTING_USER_ID);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");

        when(userService.getUserById(EXISTING_USER_ID)).thenReturn(mockUser);

        ResultActions result = mockMvc.perform(get(USER_ENDPOINT + "/{id}", EXISTING_USER_ID)
                .contentType(MediaType.APPLICATION_JSON));


        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(EXISTING_USER_ID))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

    }

    @Test
    void getUserById_shouldReturnNotFound() throws Exception {
        when(userService.getUserById(999L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        ResultActions result = mockMvc.perform(get(USER_ENDPOINT + "/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    void createUser_shouldReturnCreated() throws Exception {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password");

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@example.com");

        when(userService.createUser(any(User.class))).thenReturn(savedUser);


        String jsonUser = """
                {
                    "username": "newuser",
                    "email": "new@example.com",
                    "password": "password"
                }
                """;

        ResultActions result = mockMvc.perform(post(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser));

        result
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.username").value("newuser"));
    }


    @Test
    void updateUser_shouldReturnUpdated() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(EXISTING_USER_ID);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");


        when(userService.updateUser(
                eq(EXISTING_USER_ID),
                any(User.class)
        )).thenReturn(updatedUser);


        when(userService.createUser(any(User.class))).thenReturn(updatedUser);


        String jsonUpdate = """
        {
            "id": 1,
            "username": "updateduser",
            "email": "updated@example.com"
        }
        """;


        ResultActions result = mockMvc.perform(
                put(USER_ENDPOINT + "/{id}", EXISTING_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdate)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }


    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        when(userService.getUserById(999L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        ResultActions result = mockMvc.perform(delete(USER_ENDPOINT + "/{id}", EXISTING_USER_ID));

        result.andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(EXISTING_USER_ID);
    }


    @Test
    void deleteUser_shouldReturnNotFoundIfNotExists() throws Exception {
        when(userService.getUserById(999L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        ResultActions result = mockMvc.perform(
                delete(USER_ENDPOINT + "/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
        );


        result.andExpect(status().isNotFound());

        verify(userService, never()).deleteUser(999L);
        result.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
}