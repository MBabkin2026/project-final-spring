package org.example.projectfinalspring.controllers;

import org.example.projectfinalspring.DTO.CreateTaskDTO;
import org.example.projectfinalspring.DTO.TaskDTO;
import org.example.projectfinalspring.DTO.UpdateTaskDTO;
import org.example.projectfinalspring.entities.Task;
import org.example.projectfinalspring.entities.User;
import org.example.projectfinalspring.mappers.DTOMapper;
import org.example.projectfinalspring.services.TaskService;
import org.example.projectfinalspring.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private DTOMapper mapper;

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        User user = new User();
        user.setId(100L);

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setUser(user);


        TaskDTO dto = new TaskDTO();
        dto.setId(1L);
        dto.setTitle("Test Task");

        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));
        when(mapper.toTaskDTO(task)).thenReturn(dto);


        mockMvc.perform(get("/api/users/100/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void getTaskById_notFound() throws Exception {
        when(taskService.getTaskById(999L)).thenReturn(Optional.empty());


        mockMvc.perform(get("/api/users/100/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTasksByUser_shouldReturnList() throws Exception {

        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");


        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");

        List<Task> tasks = List.of(task1, task2);

        TaskDTO dto1 = new TaskDTO();
        dto1.setId(1L);
        dto1.setTitle("Task 1");
        TaskDTO dto2 = new TaskDTO();
        dto2.setId(2L);
        dto2.setTitle("Task 2");
        List<TaskDTO> dtos = List.of(dto1, dto2);

        when(taskService.findByUserId(100L)).thenReturn(tasks);
        when(mapper.toTaskDTO(task1)).thenReturn(dto1);
        when(mapper.toTaskDTO(task2)).thenReturn(dto2);

        mockMvc.perform(get("/api/users/100/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void createTask_shouldReturnCreated() throws Exception {

        CreateTaskDTO createDTO = new CreateTaskDTO();
        createDTO.setTitle("New Task");
        createDTO.setDescription("Description");
        createDTO.setDeadline(LocalDate.now().plusDays(7));
        createDTO.setStatus("IN_PROGRESS");

        User user = new User();
        user.setId(100L);

        Task task = new Task();
        task.setId(1L);
        task.setTitle("New Task");
        task.setUser(user);

        TaskDTO responseDTO = new TaskDTO();
        responseDTO.setId(1L);
        responseDTO.setTitle("New Task");


        when(userService.getUserById(100L)).thenReturn(user);
        when(taskService.createTask(task)).thenReturn(task);
        when(mapper.toTaskDTO(task)).thenReturn(responseDTO);



        mockMvc.perform(post("/api/users/100/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    void createTask_validationError_shouldReturnBadRequest() throws Exception {
        CreateTaskDTO createDTO = new CreateTaskDTO();


        mockMvc.perform(post("/api/users/100/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createDTO)))
                .andExpect(status().isBadRequest());
    }

    private String asJsonString(final Object obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    @Test
    void updateTask_shouldReturnUpdated() throws Exception {

        UpdateTaskDTO updateDTO = new UpdateTaskDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated Description");
        updateDTO.setDeadline(LocalDate.now().plusDays(14));
        updateDTO.setStatus("COMPLETED");



        User user = new User();
        user.setId(100L);

        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setUser(user);

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Title");
        updatedTask.setUser(user);

        TaskDTO responseDTO = new TaskDTO();
        responseDTO.setId(1L);
        responseDTO.setTitle("Updated Title");


        when(taskService.getTaskById(1L)).thenReturn(Optional.of(existingTask));
        when(taskService.updateTask(1L, updatedTask)).thenReturn(updatedTask);
        when(mapper.toTaskDTO(updatedTask)).thenReturn(responseDTO);



        mockMvc.perform(put("/api/users/100/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void updateTask_notFound_shouldReturnNotFound() throws Exception {
        UpdateTaskDTO updateDTO = new UpdateTaskDTO();
        updateDTO.setTitle("Updated");

        when(taskService.getTaskById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/100/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_validationError_shouldReturnBadRequest() throws Exception {

        UpdateTaskDTO updateDTO = new UpdateTaskDTO();
        updateDTO.setTitle("");
        updateDTO.setDescription(null);
        updateDTO.setDeadline(null);
        updateDTO.setStatus("INVALID_STATUS");

        mockMvc.perform(put("/api/users/100/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }





}