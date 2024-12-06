package com.BackEndTeam1.dto;


import com.BackEndTeam1.entity.Project;
import com.BackEndTeam1.entity.ProjectItem;
import com.BackEndTeam1.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUserDTO {
    private Long projectUserId;
    private Project project;
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
