package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.ProjectUser;
import com.BackEndTeam1.repository.ProjectUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectUserService {

    private final ProjectUserRepository projectUserRepository;


    public List<UserDTO> getParticipantsByProjectId(Long projectId) {
        List<ProjectUser> participants = projectUserRepository.findParticipantsByProjectId(projectId);

        // Stream을 사용하여 ProjectUser 리스트를 UserDTO 리스트로 변환
        return participants.stream()
                .map(UserDTO::new) // UserDTO 생성자를 호출하여 변환
                .collect(Collectors.toList());
    }
}
