package com.BackEndTeam1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamSpaceUsersDto {
    private Long teamSpaceId;
    private String teamSpaceName;
    private List<UserDTO> users;
}