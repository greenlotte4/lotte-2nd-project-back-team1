package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.*;
import com.BackEndTeam1.entity.Plan;
import com.BackEndTeam1.entity.Project;
import com.BackEndTeam1.entity.ProjectUser;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectItemRepository projectItemRepository;
    private final ProjectUserRepository projectUserRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    //선택된 프로젝트 전체 값 가져오가
    @Transactional
    public ProjectSelectDTO getAllProject(Long projectId) {
        List<Project> project = projectRepository.findAllByProjectId(projectId);
        ProjectSelectDTO projectSelectDTO = modelMapper.map(project, ProjectSelectDTO.class);
        log.info("dto 는 : " + projectSelectDTO);
        return projectSelectDTO;
    }

    // 맞는 ID를 가지고 세이브 되게 설정됨
    public ProjectDTO create(ProjectDTO projectDTO, String userId) {
        log.info("Service: Creating project with data: {}", projectDTO);
        log.info("사용자 id값은 : "+userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Project project = new Project();
        project.setUser(user);
        project.setName(projectDTO.getName());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setMaxCollaborators(projectDTO.getMaxCollaborators());
        project.setCreatedAt(Timestamp.valueOf(projectDTO.getCreatedAt()));
        project = projectRepository.save(project);

        if (projectDTO.getProjectUser() != null) {
            for (String projectUserId : projectDTO.getProjectUser()) {
                User projectUser = userRepository.findById(projectUserId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + projectUserId));
                ProjectUser projectUserEntity = new ProjectUser();
                projectUserEntity.setProject(project);
                projectUserEntity.setUser(projectUser);
                projectUserEntity.setCreatedAt(LocalDateTime.now());
                projectUserRepository.save(projectUserEntity);
            }
        }
        return modelMapper.map(project, ProjectDTO.class);
    }



    // 사용자가 만든 프로젝트를 리스트로 반환
    public List<ProjectDTO> LoginUserfindAll(String userId) {
        List<Project> projects = projectRepository.findAllByUser_UserId(userId);
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    // 사용자가 참여한 프로젝트를 리스트로 반환
    public List<ProjectDTO> InProjectFindAll(String userId) {
        List<Project> projects = projectRepository.findAllByUserProjectId(userId);
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }


    // 프로젝트 수정 메서드
    public ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO) {
        Project chProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("맞는 프로젝트 아이디 값을 찾을 수 없습니다.: " + projectId));

        // 필요한 필드만 업데이트
        if (projectDTO.getMaxCollaborators() != null) {
            chProject.setMaxCollaborators(projectDTO.getMaxCollaborators());
        }
        if (projectDTO.getStartDate() != null) {
            chProject.setStartDate(projectDTO.getStartDate());
        }
        if (projectDTO.getEndDate() != null) {
            chProject.setEndDate(projectDTO.getEndDate());
        }
        if (projectDTO.getProjectUser() != null) {
            for (String projectUserId : projectDTO.getProjectUser()) {
                projectDTO.setProjectUser(Collections.singletonList(projectUserId));
            }
        }
        if (projectDTO.getName() != null) {
            chProject.setName(projectDTO.getName());
        }

        Project updatedProject = projectRepository.save(chProject);
        return modelMapper.map(updatedProject, ProjectDTO.class);
    }

    
    //프로젝트 삭제 메서드
    public void deleteProject(Long projectId) {
        try {
            projectRepository.deleteById(projectId);
        } catch (EmptyResultDataAccessException e) {
            log.warn("No entity found with ID: {}", projectId);
        }
    }


    public ProjectDTO getProjectDetails(Long projectId) {
        // 프로젝트 가져오기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        // 프로젝트에 참여한 유저 이름 리스트 가져오기
        List<String> projectUserNames = projectUserRepository.findByProject(project)
                .stream()
                .map(projectUser -> projectUser.getUser() != null ? projectUser.getUser().getUsername() : "Unknown User")
                .toList();

        // 프로젝트 아이템과 태스크 데이터를 DTO로 변환
        List<ProjectItemDTO> projectItemDTOs = projectItemRepository.findByProject(project)
                .stream()
                .map(projectItem -> {
                    // 태스크 리스트를 DTO로 변환
                    List<TaskDTO> taskDTOs = projectItem.getTasks() != null
                            ? projectItem.getTasks().stream().map(TaskDTO::new).toList()
                            : List.of();

                    return new ProjectItemDTO(projectItem, taskDTOs);
                })
                .toList();

        // ProjectDTO 생성 및 반환
        return new ProjectDTO(project, projectUserNames, projectItemDTOs);
    }


    //생성한 프로젝트 가져오기
    public List<Project> findByUserId(String userId) {
        return projectRepository.findByUserId(userId);
    }

    //참여된 프로젝트 가져오기
    public List<ProjectUser> findByUser(String userId) {
        return projectRepository.findAllProjectUser(userId);
    }


    public void validateProjectCreation(String userId) {
        // 현재 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자 플랜 확인
        Plan plan = planRepository.findById(user.getPlan().getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("플랜 정보를 찾을 수 없습니다."));

        // 생성한 프로젝트 개수 확인
        int currentProjectCount = projectRepository.countByUserId(userId);

        // 최대 프로젝트 개수 제한 확인
        if (currentProjectCount >= plan.getMaxProject()) {
            throw new IllegalStateException("현재 플랜에서 생성 가능한 프로젝트 수를 초과했습니다. " +
                    "최대 생성 가능 수: " + plan.getMaxProject());
        }
    }

    public Project createProject(Project project) {
        validateProjectCreation(project.getUser().getUserId());
        return projectRepository.save(project);
    }

}
