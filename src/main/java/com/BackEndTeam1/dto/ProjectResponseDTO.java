package com.BackEndTeam1.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProjectResponseDTO {
    private List<ProjectDTO> createdProjects;
    private List<ProjectDTO> participatedProjects;

    public ProjectResponseDTO(List<ProjectDTO> inProjectList, List<ProjectDTO> loginUserProjectList) {
        this.createdProjects = inProjectList;
        this.participatedProjects = loginUserProjectList;
    }
}
