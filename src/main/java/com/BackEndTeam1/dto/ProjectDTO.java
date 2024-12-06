package com.BackEndTeam1.dto;


import com.BackEndTeam1.entity.Project;
import com.BackEndTeam1.entity.User;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProjectDTO(Project project) {
        this.projectId = project.getProjectId();
        this.user = project.getUser();
        this.maxCollaborators = project.getMaxCollaborators();
    }

    public String getUserId() {
        return user.getUserId();
    }

}
