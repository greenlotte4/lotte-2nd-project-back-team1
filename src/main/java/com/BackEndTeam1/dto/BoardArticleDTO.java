package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.entity.User;
import lombok.*;

import java.util.List;

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
    private String createdAt;
    private String updatedAt;
    private String userName;
    private String userId; // 작성자 ID 추가
    private String trashDate;

    private List<Long> ids;

    public BoardArticleDTO(Long id, String title, String content, String boardName, String createdAt, String updatedAt, String userName, String userId, String trashDate) {
        this.id = Math.toIntExact(id);
        this.title = title;
        this.content = content;
        this.boardName = boardName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userName = userName;
        this.userId = userId;
        this.trashDate = trashDate;

    }
}
