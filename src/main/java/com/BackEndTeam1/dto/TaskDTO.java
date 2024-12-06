package com.BackEndTeam1.dto;


import com.BackEndTeam1.entity.ProjectItem;
import com.BackEndTeam1.entity.User;
import lombok.*;
import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private Long taskId;
    private ProjectItem projectItem;
    private User assignee;
    private String name;
    private String description;
    private Integer priority;
    private String status;
    private Date startDate;
    private Date endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
