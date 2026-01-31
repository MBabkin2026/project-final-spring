package org.example.projectfinalspring.entities;

import org.junit.jupiter.api.Test;


import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    @Test
    void shouldCreateTaskWithAllFields() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setDeadline(LocalDate.of(2026, 12, 25));
        task.setStatus("PENDING");

        User user = new User();
        user.setId(100L);
        user.setUsername("testuser");
        task.setUser(user);

        assertThat(task.getId()).isEqualTo(1L);
        assertThat(task.getTitle()).isEqualTo("Test Task");
        assertThat(task.getDescription()).isEqualTo("Description");
        assertThat(task.getDeadline()).isEqualTo(LocalDate.of(2026, 12, 25));
        assertThat(task.getStatus()).isEqualTo("PENDING");
        assertThat(task.getUser().getId()).isEqualTo(100L);
    }

    @Test
    void shouldUseBuilderConstructor() {
        User user = new User();
        user.setId(200L);

        Task task = new Task(
                1L,
                "Build Task",
                "Use constructor",
                LocalDate.now(),
                "DONE",
                user
        );

        assertThat(task.getTitle()).isEqualTo("Build Task");
        assertThat(task.getUser().getId()).isEqualTo(200L);
    }

    @Test
    void shouldGenerateToString() {
        Task task = new Task();
        task.setId(5L);
        task.setTitle("Sample");
        task.setStatus("NEW");

        String str = task.toString();


        assertThat(str).contains("id=5");
        assertThat(str).contains("title='Sample'");
        assertThat(str).contains("status='NEW'");
    }

    @Test
    void equalsAndHashCodeShouldWork() {
        User user = new User();
        user.setId(1L);

        Task task1 = new Task();
        task1.setId(10L);
        task1.setTitle("Task A");
        task1.setUser(user);

        task1.setDeadline(LocalDate.now());

        task1.setStatus("IN_PROGRESS");

        Task task2 = new Task();
        task2.setId(10L);
        task2.setTitle("Task A");
        task2.setUser(user);
        task2.setDeadline(LocalDate.now());
        task2.setStatus("IN_PROGRESS");

        assertThat(task1).isEqualTo(task2);
        assertThat(task1.hashCode()).isEqualTo(task2.hashCode());
    }
}