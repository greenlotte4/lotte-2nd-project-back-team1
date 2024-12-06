package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.ProjectItemDTO;
import com.BackEndTeam1.entity.ProjectItem;
import com.BackEndTeam1.repository.ProjectItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectItemService {

    private final ProjectItemRepository projectItemRepository;
    private final ModelMapper modelMapper;

    //생성
    public ProjectItemDTO createItem(ProjectItemDTO projectItemDTO) {
        ProjectItem projectItem = projectItemRepository.save(modelMapper.map(projectItemDTO, ProjectItem.class));
        return modelMapper.map(projectItem, ProjectItemDTO.class);
    }

    //수정
    public ProjectItem updateItem(Long no, ProjectItemDTO projectItemDTO) {
        // ID로 기존의 ProjectItem 엔티티를 조회
        ProjectItem projectItem = projectItemRepository.findById(no)
                .orElseThrow(() -> new RuntimeException("맞는 프로젝트 아이디 값을 찾을 수 없습니다.: " + no));

        // 필요한 필드만 업데이트
        if (projectItemDTO.getName() != null) {
            projectItem.setName(projectItemDTO.getName());
        }
        if (projectItemDTO.getPosition() != null) {
            projectItem.setPosition(projectItemDTO.getPosition());
        }

        // 변경된 엔티티를 저장
        ProjectItem updatedProjectItem = projectItemRepository.save(projectItem);

        // 업데이트된 엔티티를 반환
        return updatedProjectItem;
    }


    //삭제
    public void deleteById(Long id){
        projectItemRepository.deleteByProjectItemId(id);
    }
}
