package com.BackEndTeam1.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProjectResponseDTO {
    private List<ProjectDTO> inProjectList;
    private List<ProjectDTO> loginUserProjectList;

    public ProjectResponseDTO(List<ProjectDTO> inProjectList, List<ProjectDTO> loginUserProjectList) {
        this.inProjectList = inProjectList;
        this.loginUserProjectList = loginUserProjectList;
    }
}
