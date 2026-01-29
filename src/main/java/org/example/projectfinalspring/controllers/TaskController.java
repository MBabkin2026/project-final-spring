package org.example.projectfinalspring.controllers;

import jakarta.validation.Valid;
import org.example.projectfinalspring.entities.Task;
import org.example.projectfinalspring.entities.User;
import org.example.projectfinalspring.services.TaskService;
import org.example.projectfinalspring.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{user_id}/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{task_id}")
    public ResponseEntity<Task> getTaskById(
            @PathVariable Long user_id,
            @PathVariable Long task_id) {

        Task task = taskService.getTaskById(task_id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasksByUser(@PathVariable Long user_id) {
        List<Task> tasks = taskService.findByUserId(user_id);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping()
    public ResponseEntity<Task> createTask(
            @PathVariable Long user_id,
            @Valid @RequestBody Task task,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        UserService userService = null;
        User user = userService.getUserById(user_id);
        task.setUser(user);

        Task createdTask = taskService.createTask(task);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);
    }

    @PutMapping("/{task_id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long user_id,
            @PathVariable Long task_id,
            @Valid @RequestBody Task taskDetails,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Task updatedTask = taskService.updateTask(task_id, taskDetails);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{task_id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long user_id,
            @PathVariable Long task_id) {

        taskService.deleteTask(task_id);
        return ResponseEntity.noContent().build();
    }
}
