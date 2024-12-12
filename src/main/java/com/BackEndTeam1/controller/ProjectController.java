package com.BackEndTeam1.controller;


import com.BackEndTeam1.dto.ProjectDTO;
import com.BackEndTeam1.dto.ProjectResponseDTO;
import com.BackEndTeam1.dto.ProjectSelectDTO;
import com.BackEndTeam1.entity.Project;
import com.BackEndTeam1.entity.ProjectItem;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.service.ProjectItemService;
import com.BackEndTeam1.service.ProjectService;
import com.BackEndTeam1.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@RestController
public class ProjectController {

    private final ProjectService projectService;


    @GetMapping("/project")
    public ResponseEntity<List<ProjectDTO>> getUserProjects(@RequestParam String userId) {
        List<Project> userProjects = projectService.findByUserId(userId);
        List<ProjectDTO> projectDTOs = userProjects.stream()
                .map(ProjectDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projectDTOs);
    }


    @PostMapping("/project/create")
    public ResponseEntity<ProjectDTO> InsertProject(
            @RequestBody ProjectDTO projectDTO,
            @RequestParam String userId
    ) {
        log.info("Received userId: {}", userId); // userId 로그 출력
        log.info("Received project data: {}", projectDTO);

        if (projectDTO.getUser() == null) {
            User user = new User();
            user.setUserId(userId);
            projectDTO.setUser(user);
        }

        ProjectDTO createdProject = projectService.create(projectDTO, userId);
        return ResponseEntity.ok(createdProject);
    }



    @GetMapping("/project/{id}")
    public ResponseEntity<?> getProjectData(@PathVariable Long id) {
        ProjectDTO projectDTO = projectService.getProjectDetails(id);
        Map<String, Object> response = new HashMap<>();
        response.put("project", projectDTO);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/project/update/{no}")
    public ResponseEntity<ProjectDTO> UpdateProject(@RequestBody ProjectDTO projectDTO ,@PathVariable Long no) {
        projectDTO = projectService.getProjectDetails(no);

        projectDTO.setName(projectDTO.getName());
        projectDTO.setProjectUser(projectDTO.getProjectUser());
        projectDTO.setUpdatedAt(LocalDateTime.now());
        projectDTO.setStartDate(projectDTO.getStartDate());
        projectDTO.setEndDate(projectDTO.getEndDate());

        projectService.updateProject(no, projectDTO);

        return ResponseEntity.ok(projectDTO);
    }

    @DeleteMapping("/project/{no}")
    public void DeleteProject(@PathVariable Long no) {
        projectService.deleteProject(no);
    }
}
