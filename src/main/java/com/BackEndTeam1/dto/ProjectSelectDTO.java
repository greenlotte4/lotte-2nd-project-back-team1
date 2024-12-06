package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Task;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSelectDTO {
    private ProjectDTO project;
    private ProjectItemDTO projectItem;
    private Task tasks;
}

