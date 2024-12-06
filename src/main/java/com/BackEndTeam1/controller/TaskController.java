package com.BackEndTeam1.controller;

import com.BackEndTeam1.entity.Task;
import com.BackEndTeam1.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/project/task")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public void createTask(@RequestBody Task task) {
        log.info("Create ItemGroup");
    }

    @PutMapping("/update/{no}")
    public void updateTask(@PathVariable int no){
        log.info("updateItemGroup");
    }

    @DeleteMapping("/delete/{no}")
    public void deleteTask(@PathVariable Long no){
        taskService.delete(no);
    }
}
