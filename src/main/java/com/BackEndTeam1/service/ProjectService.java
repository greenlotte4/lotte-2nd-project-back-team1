package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.ProjectDTO;
import com.BackEndTeam1.dto.ProjectSelectDTO;
import com.BackEndTeam1.entity.Project;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.ProjectRepository;
import com.BackEndTeam1.repository.ProjectUserRepository;
import com.BackEndTeam1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    //선택된 프로젝트 전체 값 가져오가
    @Transactional
    public ProjectSelectDTO getAllProject(Long projectId) {
        List<Project> project = projectRepository.findAllByProjectId(projectId);
        ProjectSelectDTO projectSelectDTO = modelMapper.map(project, ProjectSelectDTO.class);
        log.info("dto 는 : " + projectSelectDTO);
        return projectSelectDTO;
    }



    // 맞는 ID를 가지고 세이브 되게 설정됨
    public ProjectDTO create(ProjectDTO projectDTO) {

        log.info("Service: Creating project with data: {}", projectDTO);

        User user = userRepository.findById(projectDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + projectDTO.getUserId()));

        // Project 엔티티 생성 및 저장
        Project project = new Project();
        project.setUser(user);
        project.setName(projectDTO.getName());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setMaxCollaborators(projectDTO.getMaxCollaborators());

        project = projectRepository.save(project);

        // 저장된 Project 엔티티를 DTO로 변환하여 반환
        return modelMapper.map(project, ProjectDTO.class);
    }

    // 자기가 만든 프로젝트를 List화
    public List<ProjectDTO> LoginUserfindAll(String userId) {
        List<Project> projects = projectRepository.findAllByUser_UserId(userId);
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    // 참여된 프로젝트를 List화
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

        Project updatedProject = projectRepository.save(chProject);
        return modelMapper.map(updatedProject, ProjectDTO.class);
    }

    
    //프로젝트 삭제 메서드
    public void deleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
    }


}
