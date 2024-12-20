package com.BackEndTeam1.dto;


import com.BackEndTeam1.entity.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {

    private Long projectId;
    private User user;
    private Integer maxCollaborators = 3;
    private String name;
    private Timestamp startDate;
    private Timestamp endDate;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private String userId;
    private String userName;
    private List<String> projectUserNames;
    private List<String> projectUser;
    private List<ProjectItemDTO> projectItems;


    public ProjectDTO(Project project, List<String> projectUserNames, List<ProjectItemDTO> projectItems) {
        this.projectId = project.getProjectId();
        this.name = project.getName();
        this.userId = project.getUser().getUserId();
        this.userName = project.getUser().getUsername();
        this.maxCollaborators = project.getMaxCollaborators();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.createdAt = getCreatedAt();
        this.projectUserNames = projectUserNames;
        this.projectItems = projectItems;
    }

    public ProjectDTO(Project project) {
        this.projectId = project.getProjectId();
        this.name = project.getName();
        this.userId = project.getUser().getUserId();
        this.userName = project.getUser().getUsername();
        this.maxCollaborators = project.getMaxCollaborators();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.createdAt = getCreatedAt();
    }

    public ProjectDTO(Long projectId, String name) {
        this.projectId = projectId;
        this.name = name;
    }
}
