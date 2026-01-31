package org.example.projectfinalspring.mappers;

import org.example.projectfinalspring.DTO.TaskDTO;
import org.example.projectfinalspring.DTO.UserDTO;
import org.example.projectfinalspring.entities.Task;
import org.example.projectfinalspring.entities.User;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {
    public UserDTO toUserDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    public TaskDTO toTaskDTO(Task task) {
        if (task == null) return null;
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDeadline(task.getDeadline());
        dto.setStatus(task.getStatus());
        dto.setUser(toUserDTO(task.getUser()));
        return dto;
    }
}
