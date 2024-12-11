package com.BackEndTeam1.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDTO {
    private int board_id;
    private String board_name;
    private int max_collaborators;
    private String updated_at;
    private String created_at;
    private String user_id;

}
