package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Plan;
import lombok.*;

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
    private Plan plan_id;
    private String role;
    private String hp;
    private String addr1;
    private String addr2;
    private String zipcode;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
