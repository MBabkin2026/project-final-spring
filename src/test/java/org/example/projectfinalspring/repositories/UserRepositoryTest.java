package org.example.projectfinalspring.repositories;

import org.example.projectfinalspring.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserById() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        Optional<User> found = userRepository.findById(savedUser.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldFindUserByUsername() {
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("secret");
        user.setRole("USER");
        userRepository.save(user);

        User found = userRepository.findByUsername("john_doe");

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldNotFindUserByNonExistingUsername() {
        User found = userRepository.findByUsername("nonexistent");

        assertThat(found).isNull();
    }

    @Test
    void shouldUpdateUser() {
        User user = new User();
        user.setUsername("oldname");
        user.setEmail("old@example.com");
        user.setPassword("pass");
        user.setRole("USER");
        User saved = userRepository.save(user);

        saved.setUsername("newname");
        saved.setEmail("new@example.com");

        User updated = userRepository.save(saved);

        assertThat(updated.getUsername()).isEqualTo("newname");
        assertThat(updated.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void shouldDeleteUser() {

        User user = new User();
        user.setUsername("tobedeleted");
        user.setEmail("delete@example.com");
        user.setPassword("pass");
        user.setRole("USER");
        User saved = userRepository.save(user);

        userRepository.deleteById(saved.getId());

        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
}