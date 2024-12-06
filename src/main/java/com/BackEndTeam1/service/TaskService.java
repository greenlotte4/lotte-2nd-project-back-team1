package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.TaskDTO;
import com.BackEndTeam1.entity.Task;
import com.BackEndTeam1.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    //생성
    public Task save(TaskDTO taskDTO) {
        taskRepository.save(modelMapper.map(taskDTO, Task.class));
        return modelMapper.map(taskDTO, Task.class);
    }
    //수정
    public Task update(Long no, TaskDTO taskDTO) {
        Task task = taskRepository.findById(no).
                orElseThrow(() -> new RuntimeException("맞는 task를 찾을 수 없습니다." + no));

        if(taskDTO.getAssignee() != null) {
            task.setAsignee(taskDTO.getAssignee());
        }
        if (taskDTO.getName() != null) {
            task.setName(taskDTO.getName());
        }
        if (taskDTO.getDescription() != null) {
            task.setDescription(taskDTO.getDescription());
        }
        if (taskDTO.getPriority() != null) {
            task.setPriority(taskDTO.getPriority());
        }
        if (taskDTO.getStatus() != null) {
            task.setStatus(taskDTO.getStatus());
        }

        Task TaskUpdate = taskRepository.save(task);
        return TaskUpdate;
    }

    //삭제
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
