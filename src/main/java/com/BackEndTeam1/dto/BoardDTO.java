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
    @Builder.Default
    private int max_collaborators = 3;
    private String updated_at;
    private String created_at;
    private String user_id;

}
