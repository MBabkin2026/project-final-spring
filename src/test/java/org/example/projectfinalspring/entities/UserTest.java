package org.example.projectfinalspring.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void shouldCreateUserWithAllFields() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("secret123");
        user.setRole("ADMIN");

        List<Task> tasks = Arrays.asList(
                new Task(1L, "Task 1", "Desc 1", LocalDate.now(), "PENDING", user),
                new Task(2L, "Task 2", "Desc 2", LocalDate.now().plusDays(1), "NEW", user)
        );
        user.setTasks(tasks);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("secret123");
        assertThat(user.getRole()).isEqualTo("ADMIN");
        assertThat(user.getTasks()).hasSize(2);
        assertThat(user.getTasks().get(0).getTitle()).isEqualTo("Task 1");
    }

    @Test
    void shouldUseBuilderConstructor() {
        Task task = new Task();
        task.setTitle("Single Task");

        List<Task> tasks = List.of(task);

        User user = new User(1L, "builder", "builder@example.com", "pass", "USER", tasks);

        assertThat(user.getUsername()).isEqualTo("builder");
        assertThat(user.getTasks()).contains(task);
    }

    @Test
    void toStringShouldIncludeAllFields() {
        User user = new User();
        user.setId(5L);
        user.setUsername("sample");
        user.setEmail("sample@example.com");

        String str = user.toString();

        assertThat(str).contains("id=5");
        assertThat(str).contains("username='sample'");
        assertThat(str).contains("email='sample@example.com'");
    }

    @Test
    void equalsAndHashCodeShouldWork() {
        User user1 = new User();
        user1.setId(10L);
        user1.setUsername("alice");
        user1.setEmail("alice@example.com");

        user1.setPassword("pass");
        user1.setRole("USER");

        user1.setTasks(List.of());


        User user2 = new User();
        user2.setId(10L);
        user2.setUsername("alice");
        user2.setEmail("alice@example.com");
        user2.setPassword("pass");
        user2.setRole("USER");
        user2.setTasks(List.of());

        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }
}