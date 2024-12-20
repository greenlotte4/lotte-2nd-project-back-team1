package com.BackEndTeam1.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {

    private String userId;
    private Integer commentId;
    private String content;
    private String createdAt;
    private Integer boardArticleId; // 게시글 ID


    public CommentDTO(Integer commentId, String username, String content, Timestamp createdAt, Integer boardArticleId) {
        this.userId = username;
        this.content = content;
        this.createdAt = createdAt.toString();
        this.commentId = commentId;
        this.boardArticleId = boardArticleId;
    }
}
