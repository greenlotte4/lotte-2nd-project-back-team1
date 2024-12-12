package com.BackEndTeam1.dto;


import com.BackEndTeam1.entity.Project;
import com.BackEndTeam1.entity.ProjectItem;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProjectItemDTO {
    private Long projectItemId;
    private Project project;
    private String name;
    private Integer position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TaskDTO> tasks;

    public ProjectItemDTO(ProjectItem projectItem, List<TaskDTO> tasks) {
        this.projectItemId = projectItem.getProjectItemId();
        this.name = projectItem.getName();
        this.position = projectItem.getPosition();
        this.tasks = tasks;
    }
}
