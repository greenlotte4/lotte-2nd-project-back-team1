package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.TaskDTO;
import com.BackEndTeam1.entity.Task;
import com.BackEndTeam1.entity.User;
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
            // `User` 엔티티를 데이터베이스에서 조회하여 영속 상태로 만듦
            User assignee = userRepository.findById(taskDTO.getAssignee())
                    .orElseThrow(() -> new RuntimeException("User ID를 찾을 수 없습니다: " + taskDTO.getAssignee()));
            existingTask.setAsignee(assignee.getUserId()); // 영속 상태의 `User` 설정
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
}
