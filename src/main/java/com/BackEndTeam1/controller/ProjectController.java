package com.BackEndTeam1.controller;


import com.BackEndTeam1.dto.ProjectDTO;
import com.BackEndTeam1.dto.ProjectResponseDTO;
import com.BackEndTeam1.dto.ProjectUserDTO;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.Plan;
import com.BackEndTeam1.entity.Project;
import com.BackEndTeam1.entity.ProjectUser;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.PlanRepository;
import com.BackEndTeam1.repository.ProjectRepository;
import com.BackEndTeam1.repository.UserRepository;
import com.BackEndTeam1.service.ProjectService;
import com.BackEndTeam1.service.ProjectUserService;
import com.BackEndTeam1.service.UserService;
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
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final ProjectUserService projectUserService;

    @GetMapping("/project")
    public ResponseEntity<ProjectResponseDTO> getUserProjects(@RequestParam String userId) {
        List<ProjectUser> projectUsers = projectRepository.findAllProjectUser(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Plan plan = planRepository.findById(user.getPlan().getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("플랜 정보를 찾을 수 없습니다."));

        List<Project> userProjects = projectService.findByUserId(userId);

        int maxProjects = plan.getPlanId() == 1 ? 3 :
                plan.getPlanId() == 2 ? 10 :
                        plan.getPlanId() == 3 ? 1000 : 0;

        if (userProjects.size() > maxProjects) {
            throw new IllegalStateException("현재 플랜에서 생성 가능한 최대 프로젝트 수를 초과했습니다. " +
                    "최대 생성 가능 수: " + maxProjects);
        }

        List<ProjectDTO> createdProjects = userProjects.stream()
                .limit(maxProjects)
                .map(ProjectDTO::new)
                .collect(Collectors.toList());
        List<ProjectDTO> participatedProjects = projectUsers.stream()
                .map(pu -> new ProjectDTO(pu.getProject().getProjectId(), pu.getProject().getName()))
                .collect(Collectors.toList());
        ProjectResponseDTO responseDTO = new ProjectResponseDTO();
        responseDTO.setCreatedProjects(createdProjects);
        responseDTO.setParticipatedProjects(participatedProjects);

        return ResponseEntity.ok(responseDTO);
    }




    @PostMapping("/project/create")
    public ResponseEntity<ProjectDTO> InsertProject(
            @RequestBody ProjectDTO projectDTO,
            @RequestParam String userId
    ) {
        log.info("Received userId: {}", userId); // userId 로그 출력
        log.info("Received project data: {}", projectDTO);

        if (projectDTO.getUserId() == null) {
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
        System.out.println("Returned Project DTO: " + projectDTO); // 전체 데이터 로그 출력
        Map<String, Object> response = new HashMap<>();
        response.put("project", projectDTO);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/project/update/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
            @RequestBody ProjectDTO projectDTO, @PathVariable Long id) {
        if (!id.equals(projectDTO.getProjectId())) {
            throw new RuntimeException("경로 ID와 요청 바디 ID가 일치하지 않습니다.");
        }
        projectDTO.setUpdatedAt(LocalDateTime.now());
        projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(projectDTO);
    }


    @DeleteMapping("/project/delete/{id}")
    public ResponseEntity<?> DeleteProject(@PathVariable("id") Long id) {
        try {
            log.info("Deleting project with ID: {}", id);
            projectService.deleteProject(id);
            return ResponseEntity.ok().build();
        } catch (NumberFormatException e) {
            log.warn("Invalid project ID: {}", id);
            return ResponseEntity.badRequest().body("Invalid project ID: must be a number.");
        }
    }

    @GetMapping("/project/{projectId}/participants")
    public ResponseEntity<List<UserDTO>> getProjectParticipants(@PathVariable Long projectId) {
        List<UserDTO> participants = projectUserService.getParticipantsByProjectId(projectId);
        return ResponseEntity.ok(participants); // JSON 응답
    }


}
