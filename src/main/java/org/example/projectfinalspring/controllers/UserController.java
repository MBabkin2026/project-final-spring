package org.example.projectfinalspring.controllers;

import jakarta.validation.Valid;
import org.example.projectfinalspring.DTO.CreateUserDTO;
import org.example.projectfinalspring.DTO.UpdateUserDTO;
import org.example.projectfinalspring.DTO.UserDTO;
import org.example.projectfinalspring.entities.User;
import org.example.projectfinalspring.mappers.DTOMapper;
import org.example.projectfinalspring.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final DTOMapper mapper;

    public UserController(UserService userService, DTOMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long user_id) {
        User user = userService.getUserById(user_id);
        return ResponseEntity.ok(mapper.toUserDTO(user));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> dtos = users.stream()
                .map(mapper::toUserDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody CreateUserDTO createDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User();
        user.setUsername(createDTO.getUsername());
        user.setEmail(createDTO.getEmail());
        user.setPassword(createDTO.getPassword());  // Добавьте шифрование!
        user.setRole("USER");

        User createdUser = userService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toUserDTO(createdUser));
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long user_id,
            @Valid @RequestBody UpdateUserDTO updateDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.getUserById(user_id);
        user.setUsername(updateDTO.getUsername());
        user.setEmail(updateDTO.getEmail());

        User updatedUser = userService.updateUser(user_id, user);
        return ResponseEntity.ok(mapper.toUserDTO(updatedUser));
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long user_id) {
        userService.deleteUser(user_id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> findByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toUserDTO(user));
    }
}