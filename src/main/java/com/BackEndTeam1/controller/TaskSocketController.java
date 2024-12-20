package com.BackEndTeam1.controller;


import com.BackEndTeam1.dto.TaskDTO;
import com.BackEndTeam1.entity.Task;
import com.BackEndTeam1.service.TaskService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class TaskSocketController {

    private final TaskService taskService;
    private final ModelMapper modelMapper;

    @MessageMapping("/tasks/update")
    @SendTo("/sub/tasks/update")
    public TaskDTO handleTaskUpdate(@Payload TaskDTO taskDTO) {
        System.out.println("응답 TaskDTO: " + taskDTO);
        Task updatedTask = taskService.updateTask(taskDTO.getTaskId(), taskDTO);
        return modelMapper.map(updatedTask, TaskDTO.class);
    }

    @MessageMapping("/tasks/delete")
    @SendTo("/sub/tasks/delete")
    public void handleTaskDelete(@Payload TaskDTO taskDTO) {
        taskService.delete(taskDTO.getTaskId());
    }

}
