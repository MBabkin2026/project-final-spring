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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole("USER");

        testUser = userRepository.save(testUser);
        // Явный flush не нужен: save() уже синхронизирует с БД
    }

    @Test
    void shouldSaveTaskWithValidData() {
        Task task = new Task();
        task.setTitle("Learn JPA");
        task.setDescription("Read documentation and write tests.");
        task.setDeadline(LocalDate.now().plusDays(5));
        task.setStatus("PENDING");
        task.setUser(testUser);

        Task savedTask = taskRepository.save(task);

        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Learn JPA");
        assertThat(savedTask.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void shouldFindTaskById() {
        Task task = new Task();
        task.setTitle("Write Unit Tests");
        task.setDeadline(LocalDate.now());
        task.setStatus("IN_PROGRESS");
        task.setUser(testUser);

        Task savedTask = taskRepository.save(task);

        Optional<Task> found = taskRepository.findById(savedTask.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Write Unit Tests");
    }

    @Test
    void shouldNotFindTaskByNonExistingId() {
        Optional<Task> found = taskRepository.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllTasksByUserId() {
        Task task1 = new Task();
        task1.setTitle("Task One");
        task1.setDeadline(LocalDate.now());
        task1.setStatus("DONE");
        task1.setUser(testUser);

        Task task2 = new Task();
        task2.setTitle("Task Two");
        task2.setDeadline(LocalDate.now().plusDays(2));
        task2.setStatus("PENDING");
        task2.setUser(testUser);

        taskRepository.save(task1);
        taskRepository.save(task2);

        List<Task> tasks = taskRepository.findByUserId(testUser.getId());

        assertThat(tasks).hasSize(2);
        assertThat(tasks)
                .extracting("title")
                .containsExactlyInAnyOrder("Task One", "Task Two");
    }

    @Test
    void shouldReturnEmptyListWhenNoTasksForUser() {
        List<Task> tasks = taskRepository.findByUserId(888L); // несуществующий ID
        assertThat(tasks).isEmpty();
    }

    @Test
    void shouldUpdateTaskFields() {
        Task task = new Task();
        task.setTitle("Initial Title");
        task.setDescription("Old description.");
        task.setDeadline(LocalDate.now());
        task.setStatus("NEW");
        task.setUser(testUser);

        Task savedTask = taskRepository.save(task);

        savedTask.setTitle("Updated Title");
        savedTask.setDescription("Updated description.");
        savedTask.setStatus("IN_PROGRESS");

        Task updatedTask = taskRepository.save(savedTask);

        assertThat(updatedTask.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTask.getDescription()).isEqualTo("Updated description.");
        assertThat(updatedTask.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void shouldDeleteTaskById() {
        Task task = new Task();
        task.setTitle("To Be Deleted");
        task.setDeadline(LocalDate.now());
        task.setStatus("PENDING");
        task.setUser(testUser);

        Task savedTask = taskRepository.save(task);

        taskRepository.deleteById(savedTask.getId());

        Optional<Task> found = taskRepository.findById(savedTask.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void shouldPreserveUserWhenDeletingTask() {
        Task task = new Task();
        task.setTitle("Independent Task");
        task.setDeadline(LocalDate.now());
        task.setStatus("COMPLETED");
        task.setUser(testUser);

        Task savedTask = taskRepository.save(task);

        taskRepository.deleteById(savedTask.getId());

        Optional<User> userFound = userRepository.findById(testUser.getId());
        assertThat(userFound).isPresent();
        assertThat(userFound.get().getUsername()).isEqualTo("testuser");
    }
}