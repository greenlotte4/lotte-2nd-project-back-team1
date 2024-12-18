package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.ProjectItemDTO;
import com.BackEndTeam1.dto.TaskDTO;
import com.BackEndTeam1.entity.Task;
import com.BackEndTeam1.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/project/task")
public class TaskController {

    private final TaskService taskService;
    private final ModelMapper modelMapper;

    @PostMapping("/create")
    public void createTask(@RequestBody Task task) {
        taskService.createTask(modelMapper.map(task, TaskDTO.class));
    }

    @PutMapping("/update/{no}")
    public ResponseEntity<?> updateTask(@PathVariable Long no, @RequestBody TaskDTO taskDTO) {
        try {
            // Task 업데이트 호출
            Task updatedTask = taskService.updateTask(no, taskDTO);

            // 성공적으로 업데이트된 Task를 반환
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            // 실패 시 오류 메시지와 상태 코드 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task 업데이트 실패: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{no}")
    public void deleteTask(@PathVariable Long no){
        taskService.delete(no);
    }
}
