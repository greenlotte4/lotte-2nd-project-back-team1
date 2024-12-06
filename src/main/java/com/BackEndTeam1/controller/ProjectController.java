package com.BackEndTeam1.controller;


import com.BackEndTeam1.dto.ProjectDTO;
import com.BackEndTeam1.dto.ProjectResponseDTO;
import com.BackEndTeam1.dto.ProjectSelectDTO;
import com.BackEndTeam1.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/project")
    public ResponseEntity<ProjectResponseDTO> selectAllProject(Principal principal) {
        List<ProjectDTO> inProjectList = projectService.InProjectFindAll(principal.getName());
        List<ProjectDTO> loginUserProjectList = projectService.LoginUserfindAll(principal.getName());
        ProjectResponseDTO response = new ProjectResponseDTO(inProjectList, loginUserProjectList);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/project/create")
    public ResponseEntity<ProjectDTO> InsertProject(@RequestBody ProjectDTO projectDTO) {
        log.info("Received project creation request: {}", projectDTO);
        // userId가 포함되어 있는지 확인
        if (projectDTO.getUser() == null || projectDTO.getUser().getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        ProjectDTO createdProject = projectService.create(projectDTO);
        return ResponseEntity.ok(createdProject);
    }

    @GetMapping("/project/{no}")
    public ResponseEntity<ProjectSelectDTO> selectAllProjectId(@PathVariable Long no) {
        ProjectSelectDTO result = projectService.getAllProject(no);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/project/update/{no}")
    public void UpdateProject(@PathVariable int no) {
        log.info("Update Project");
    }

    @DeleteMapping("/project/delete/{no}")
    public void DeleteProject(@PathVariable Long no) {
        projectService.deleteProject(no);
    }

}
