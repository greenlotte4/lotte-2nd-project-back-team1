package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Plan;
import com.BackEndTeam1.entity.User;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanHistoryDTO {
    private Long planHistoryId;
    private User user;
    private String userId;  // User의 ID
    private Plan plan;
    private Long planId;  // Plan의 ID
    private LocalDate startDate;
    private LocalDate endDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
