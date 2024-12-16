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

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    //생성
    public Task createTask(TaskDTO taskDTO) {
        taskRepository.save(modelMapper.map(taskDTO, Task.class));
        return modelMapper.map(taskDTO, Task.class);
    }
    //수정
    public Task updateTask(Long taskId, TaskDTO taskDTO) {
        // Task를 ID로 조회
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task ID를 찾을 수 없습니다: " + taskId));

        // DTO의 데이터를 Task 엔티티에 매핑
        if (taskDTO.getName() != null) {
            existingTask.setName(taskDTO.getName());
        }
        if (taskDTO.getAssignee() != null) {
            User assignee = userRepository.findByUserId(taskDTO.getAssignee())
                    .orElseThrow(() -> new RuntimeException("유효하지 않은 사용자 ID: " + taskDTO.getAssignee()));
            existingTask.setAsignee(assignee.getUserId());
        }
        if (taskDTO.getStatus() != null) {
            existingTask.setStatus(taskDTO.getStatus());
        }
        if (taskDTO.getStartDate() != null) {
            existingTask.setStartDate(taskDTO.getStartDate());
        }
        if (taskDTO.getEndDate() != null) {
            existingTask.setEndDate(taskDTO.getEndDate());
        }
        if (taskDTO.getDescription() != null) {
            existingTask.setDescription(taskDTO.getDescription());
        }
        if (taskDTO.getPriority() != null) {
            existingTask.setPriority(taskDTO.getPriority());
        }

        // 업데이트된 Task 저장
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
