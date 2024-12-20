package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Plan;
import com.BackEndTeam1.entity.ProjectUser;
import com.BackEndTeam1.entity.User;
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
    private String statusMessage; // 상태 메시지
    private String profile;
    private String userStatus;

    private PlanHistoryDTO planHistory;
    private ProjectUser projectUser;

    public UserDTO(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public UserDTO(ProjectUser projectUser) {
        if (projectUser.getUser() != null) { // Null 체크
            System.out.println("ProjectUser's User ID: " + projectUser.getUser().getUserId());
            System.out.println("ProjectUser's Username: " + projectUser.getUser().getUsername());
            this.userId = projectUser.getUser().getUserId();
            this.username = projectUser.getUser().getUsername();
        } else {
            System.out.println("ProjectUser's User is null!");
        }
    }

    public UserDTO(String userId, String username, String userStatus) {
        this.userId = userId;
        this.username = username;
        this.userStatus = userStatus;
    }

    public UserDTO(String userId, String username, String profile, String userStatus) {
        this.userId = userId;
        this.username = username;
        this.profile = profile;
        this.userStatus = userStatus;
    }
}
