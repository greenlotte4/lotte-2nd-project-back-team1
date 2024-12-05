package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Plan;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String userId;
    private String pass;
    private String username;
    private String email;
    private Plan plan;
    private String role;
    private String hp;
    private String addr1;
    private String addr2;
    private String zipcode;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private PlanHistoryDTO planHistory;
}
