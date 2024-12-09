package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TeamSpaceDTO {
    private Long teamSpaceId;
    private String roomname;
    private String serialnumber;
    private LocalDate endDate;
    private User user;
}
