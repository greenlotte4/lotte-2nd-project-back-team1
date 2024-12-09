package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.TeamSpace;
import com.BackEndTeam1.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TeamSpaceMemberDTO {
    private Long teamSpaceMemberId;
    private User user;
    private TeamSpace teamSpace;
}
