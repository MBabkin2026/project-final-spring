package org.example.projectfinalspring.repositories;

import org.example.projectfinalspring.entities.Task;
import org.example.projectfinalspring.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDate;
import java.util.Optional;


import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("repo_user");
        testUser.setEmail("repo@test.com");
        testUser.setPassword("password123");
        testUser.setRole("USER");

        testUser = userRepository.save(testUser);
    }

    @Test
    void saveUser_shouldPersistToDatabase() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        user.setPassword("newpass");
        user.setRole("MODERATOR");

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("newuser");
        assertThat(saved.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void findById_shouldReturnUserIfExists() {
        Optional<User> found = userRepository.findById(testUser.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("repo_user");
    }

    @Test
    void findById_shouldReturnEmptyIfNotExists() {
        Optional<User> found = userRepository.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void findByUsername_shouldReturnUser() {
        User found = userRepository.findByUsername("repo_user");

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("repo@test.com");
    }

    @Test
    void findByUsername_shouldReturnNullForNonexistent() {
        User found = userRepository.findByUsername("nonexistent");
        assertThat(found).isNull();
    }

    @Test
    void updateUser_shouldChangeFields() {
        testUser.setUsername("updated_name");
        testUser.setEmail("updated@example.com");
        testUser.setRole("ADMIN");


        User updated = userRepository.save(testUser);

        assertThat(updated.getUsername()).isEqualTo("updated_name");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
        assertThat(updated.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void deleteById_shouldRemoveUserFromDatabase() {
        Long id = testUser.getId();

        userRepository.deleteById(id);

        Optional<User> found = userRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void deletingUser_shouldAlsoDeleteAssociatedTasks() {
        Task task = new Task();
        task.setTitle("User's Task");
        task.setDescription("This task belongs to the user");
        task.setDeadline(LocalDate.now().plusDays(3));
        task.setStatus("PENDING");
        task.setUser(testUser);

        Task savedTask = taskRepository.save(task);

        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getUser().getId()).isEqualTo(testUser.getId());


        userRepository.deleteById(testUser.getId());

        Optional<User> foundUser = userRepository.findById(testUser.getId());
        assertThat(foundUser).isEmpty();

        Optional<Task> deletedTask = taskRepository.findById(savedTask.getId());
        assertThat(deletedTask).isEmpty();
    }
}