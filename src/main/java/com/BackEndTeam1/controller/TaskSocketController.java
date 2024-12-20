package com.BackEndTeam1.controller;


import com.BackEndTeam1.dto.ProjectItemDTO;
import com.BackEndTeam1.dto.TaskDTO;
import com.BackEndTeam1.entity.ProjectItem;
import com.BackEndTeam1.entity.Task;
import com.BackEndTeam1.service.ProjectItemService;
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
    private final ProjectItemService projectItemService;
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

    @MessageMapping("/tasks/move")
    @SendTo("/sub/tasks/update")
    public TaskDTO handleTaskMove(@Payload TaskDTO taskDTO) {
        System.out.println("Received TaskDTO: " + taskDTO);

        Long taskId = taskDTO.getTaskId();
        Long groupId = taskDTO.getGroupId(); // 수정된 TaskDTO에서 groupId 가져오기

        if (groupId == null || taskId == null) {
            System.err.println("TaskDTO의 groupId 또는 taskId가 null입니다: " + taskDTO);
            throw new IllegalArgumentException("TaskDTO의 groupId 또는 taskId가 null입니다: " + taskDTO);
        }

        System.out.println("Task 이동 요청: taskId=" + taskId + ", groupId=" + groupId);

        Task updatedTask = taskService.updateTaskGroup(taskId, groupId);
        return modelMapper.map(updatedTask, TaskDTO.class);
    }

    @MessageMapping("/group/move")
    @SendTo("/sub/group/update")
    public ProjectItemDTO handleGroupMove(@Payload ProjectItemDTO projectItemDTO) {
        Long groupId = projectItemDTO.getProjectItemId();
        Integer newPosition = projectItemDTO.getPosition();

        if (groupId == null || newPosition == null) {
            throw new IllegalArgumentException("Group ID 또는 Position 값이 누락되었습니다.");
        }

        ProjectItem updatedGroup = projectItemService.updateGroupPosition(groupId, newPosition);

        return modelMapper.map(updatedGroup, ProjectItemDTO.class);
    }




}
