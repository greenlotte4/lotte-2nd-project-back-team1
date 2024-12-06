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
    private String userName;

    public BoardArticleDTO(Long id, String title, String content, String boardName, String created_At, String updated_At, String userName) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.content = content;
        this.boardName = boardName;
        this.created_At = created_At;
        this.updated_At = updated_At;
        this.userName = userName;

    }
}
