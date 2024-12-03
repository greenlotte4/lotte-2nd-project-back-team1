package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.entity.User;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardArticleDTO {

    private int id;
    private String title;
    private String content;
    private String boardName;
    private String created_At;
    private String updated_At;
}
