package org.example.projectfinalspring.repositories;

import org.example.projectfinalspring.entities.Task;
import org.example.projectfinalspring.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TaskRepositoryIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("repo_user");
        testUser.setEmail("repo@test.com");
        testUser.setPassword("pass123");
        testUser.setRole("USER");

        testUser = userRepository.save(testUser);
        // Явный flush не требуется — save() автоматически синхронизирует с БД
    }

    @Test
    void saveTask_shouldPersistToDatabase() {
        Task task = new Task();
        task.setTitle("New Task");
        task.setDescription("Save to DB");
        task.setDeadline(LocalDate.now().plusDays(3));
        task.setStatus("NEW");
        task.setUser(testUser);

        Task saved = taskRepository.save(task);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("New Task");
        assertThat(saved.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void findById_shouldReturnTaskIfExists() {
        Task task = createAndSaveTask("Find by ID");

        Optional<Task> found = taskRepository.findById(task.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Find by ID");
    }

    @Test
    void findById_shouldReturnEmptyIfNotExists() {
        Optional<Task> found = taskRepository.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void findByUserId_shouldReturnAllTasksForUser() {
        createAndSaveTask("Task 1");
        createAndSaveTask("Task 2");

        List<Task> tasks = taskRepository.findByUserId(testUser.getId());

        assertThat(tasks).hasSize(2);
        assertThat(tasks)
                .extracting("title")
                .containsExactlyInAnyOrder("Task 1", "Task 2");
    }

    @Test
    void findByUserId_shouldReturnEmptyListForNonexistentUser() {
        List<Task> tasks = taskRepository.findByUserId(888L);
        assertThat(tasks).isEmpty();
    }

    @Test
    void updateTask_shouldChangeFields() {
        Task task = createAndSaveTask("To Update");

        task.setTitle("Updated Title");
        task.setStatus("COMPLETED");
        Task updated = taskRepository.save(task);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void deleteById_shouldRemoveTaskFromDatabase() {
        Task task = createAndSaveTask("To Delete");
        Long id = task.getId();

        taskRepository.deleteById(id);

        Optional<Task> found = taskRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void deletingTask_shouldNotDeleteUser() {
        Task task = createAndSaveTask("Keep User");
        Long taskId = task.getId();
        Long userId = testUser.getId();

        taskRepository.deleteById(taskId);

        Optional<Task> deletedTask = taskRepository.findById(taskId);
        assertThat(deletedTask).isEmpty();

        Optional<User> user = userRepository.findById(userId);
        assertThat(user).isPresent();
        assertThat(user.get().getUsername()).isEqualTo("repo_user");
        assertThat(user.get().getEmail()).isEqualTo("repo@test.com");
    }

    private Task createAndSaveTask(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("Auto-generated for test");
        task.setDeadline(LocalDate.now().plusDays(1));
        task.setStatus("NEW");
        task.setUser(testUser);

        return taskRepository.save(task);
    }
}