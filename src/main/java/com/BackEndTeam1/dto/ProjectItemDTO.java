package com.BackEndTeam1.dto;


import com.BackEndTeam1.entity.Project;
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
}
