package com.BackEndTeam1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDTO {
    private int board_id;
    @JsonProperty("boardName")
    private String board_name;
    @Builder.Default
    private int max_collaborators = 3;
    private String updated_at;
    private String created_at;
    private String user_id;

}
