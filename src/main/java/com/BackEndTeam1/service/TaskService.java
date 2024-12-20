package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.TaskDTO;
import com.BackEndTeam1.entity.ProjectItem;
import com.BackEndTeam1.entity.Task;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.ProjectItemRepository;
import com.BackEndTeam1.repository.TaskRepository;
import com.BackEndTeam1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final ProjectItemRepository projectItemRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    //생성
    public Task createTask(TaskDTO taskDTO) {
        Task task = modelMapper.map(taskDTO, Task.class);
        if (taskDTO.getStartDate() != null && taskDTO.getEndDate() != null) {
            long totalDuration = taskDTO.getEndDate().getTime() - taskDTO.getStartDate().getTime();
            long elapsedDuration = System.currentTimeMillis() - taskDTO.getStartDate().getTime();

            int progress = totalDuration > 0
                    ? (int) Math.min(Math.max((elapsedDuration / (double) totalDuration) * 100, 0), 100)
                    : 0;

            task.setPriority(progress);
        } else {
            task.setPriority(0);
        }
        taskRepository.save(task);
        return task;
    }

    //수정
    public Task updateTask(Long taskId, TaskDTO taskDTO) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task ID를 찾을 수 없습니다: " + taskId));

        if (taskDTO.getName() != null) {
            existingTask.setName(taskDTO.getName());
        }
        if (taskDTO.getStartDate() != null) {
            existingTask.setStartDate(taskDTO.getStartDate());
        }
        if (taskDTO.getEndDate() != null) {
            existingTask.setEndDate(taskDTO.getEndDate());
        }
        if (taskDTO.getPriority() != null) {
            existingTask.setPriority(taskDTO.getPriority());
        }
        if (taskDTO.getAssignee() != null) {
            User assignee;
            try {
                // Assignee가 ID일 경우
                String assigneeId = taskDTO.getAssignee();
                assignee = userRepository.findById(assigneeId)
                        .orElseThrow(() -> new RuntimeException("User ID를 찾을 수 없습니다: " + taskDTO.getAssignee()));
            } catch (NumberFormatException e) {
                // Assignee가 이름일 경우
                assignee = userRepository.findByUsername(taskDTO.getAssignee())
                        .orElseThrow(() -> new RuntimeException("User 이름을 찾을 수 없습니다: " + taskDTO.getAssignee()));
            }
            existingTask.setAsignee(assignee.getUserId());
        }


        return taskRepository.save(existingTask);
    }


    //삭제
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    public Object findByProjectItemId(Long projectItemId) {
        Object tasks = taskRepository.findAllByProjectItem_ProjectItemId(projectItemId);
        return tasks;
    }

    
    // task 이동
    public Task updateTaskGroup(Long taskId, Long groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID가 null입니다.");
        }

        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task ID를 찾을 수 없습니다: " + taskId));

        ProjectItem projectItem = projectItemRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("ProjectItem ID를 찾을 수 없습니다: " + groupId));

        existingTask.setProjectItem(projectItem);
        return taskRepository.save(existingTask);
    }

}
