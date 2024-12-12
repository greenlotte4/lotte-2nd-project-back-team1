package com.BackEndTeam1.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUserDTO {
    private Long projectUserId;
    private Long projectId;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
