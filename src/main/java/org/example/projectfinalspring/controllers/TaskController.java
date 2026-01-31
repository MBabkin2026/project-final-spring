package org.example.projectfinalspring.controllers;

import jakarta.validation.Valid;
import org.example.projectfinalspring.DTO.CreateTaskDTO;
import org.example.projectfinalspring.DTO.TaskDTO;
import org.example.projectfinalspring.DTO.UpdateTaskDTO;
import org.example.projectfinalspring.entities.Task;
import org.example.projectfinalspring.entities.User;
import org.example.projectfinalspring.mappers.DTOMapper;
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
    private final UserService userService;
    private final DTOMapper mapper;

    public TaskController(TaskService taskService, UserService userService, DTOMapper mapper) {
        this.taskService = taskService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping("/{task_id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long task_id) {
        Task task = taskService.getTaskById(task_id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return ResponseEntity.ok(mapper.toTaskDTO(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasksByUser(@PathVariable Long user_id) {
        List<Task> tasks = taskService.findByUserId(user_id);
        List<TaskDTO> dtos = tasks.stream()
                .map(mapper::toTaskDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @PathVariable Long user_id,
            @Valid @RequestBody CreateTaskDTO createDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.getUserById(user_id);
        Task task = new Task();
        task.setTitle(createDTO.getTitle());
        task.setDescription(createDTO.getDescription());
        task.setDeadline(createDTO.getDeadline());
        task.setStatus(createDTO.getStatus());
        task.setUser(user);

        Task createdTask = taskService.createTask(task);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toTaskDTO(createdTask));
    }

    @PutMapping("/{task_id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long task_id,
            @Valid @RequestBody UpdateTaskDTO updateDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Task task = taskService.getTaskById(task_id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(updateDTO.getTitle());
        task.setDescription(updateDTO.getDescription());
        task.setDeadline(updateDTO.getDeadline());
        task.setStatus(updateDTO.getStatus());

        Task updatedTask = taskService.updateTask(task_id, task);
        return ResponseEntity.ok(mapper.toTaskDTO(updatedTask));
    }

    @DeleteMapping("/{task_id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long task_id) {
        taskService.deleteTask(task_id);
        return ResponseEntity.noContent().build();
    }
}
